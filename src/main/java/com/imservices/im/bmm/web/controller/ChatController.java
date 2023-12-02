package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.bean.ChatTxtBean;
import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.MsgHistoryEntity;
import com.imservices.im.bmm.service.ChatService;
import com.imservices.im.bmm.utils.FTPUtil;
import com.imservices.im.bmm.utils.spring.PropertiesConfig;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import com.imservices.im.bmm.websocket.cmd.UserChatCmd;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller("chatController")
@RequestMapping(value = "/chat")
@CrossOrigin
public class ChatController {

	@Autowired
	private UserChatCmd userChatCmd;
	@Autowired
	private ChatService chatService;
	
	
	@RequestMapping(value={"/sendQrcode"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void sendQrcode(HttpServletRequest request, HttpServletResponse response) {
		try {
			String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
			String mids = request.getParameter("mids");
			String base64 = request.getParameter("base64");
			if(StringUtils.isEmpty(mids)) return;
			base64 = base64.replaceAll("data:image/jpeg;base64,", "");
			String fn = UUID.randomUUID().toString().replaceAll("-", "");
			decryptByBase64(base64,request.getRealPath("/images/upload/chat/uploadB64Img/")+fn+".jpg");
			
			   
			String[] ids = mids.split(",");
			for(String mid : ids) {
				if(StringUtils.isEmpty(mids)) continue;
				ChatTxtBean bean = new ChatTxtBean();
				bean.setFromUid(MEMBERID);
				bean.setTxt("<img  style='max-width: 120px;max-height:120px;width:100%;' class='face' src='"+PropertiesConfig.getResourceHttpPrefix()+"/"+FTPUtil.FTP_FOLDER.chat_img+"/"+fn+".jpg'>");
				bean.setPsr("uparse");  
				bean.setToUid(mid);
				userChatCmd.sendTXT(bean);
			} 
			 
			ResponseUtils.json(response, 200, "", null);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	
	
	
	public static String decryptByBase64(String base64, String filePath) {
		if (base64 == null && filePath == null) {
            return "生成文件失败，请给出相应的数据。";
		}
		try {
			Files.write(Paths.get(filePath), Base64.getDecoder().decode(base64),StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "指定路径下生成文件成功！";
	}


	@AuthPassport
	@RequestMapping(value = "/listPage",method = {RequestMethod.POST,RequestMethod.OPTIONS})
	public void listPage(HttpServletRequest request,HttpServletResponse response) {
		String forMemberId = request.getHeader("x-access-uid");
        String toMemberId = request.getParameter("toMemberid");
		log.error("forMemberId:::{}",forMemberId);
		log.error("toMemberId:::{}",toMemberId);

		int maxLen = 30;
		String pageSizeString = request.getParameter("pageSize");
		if (StringUtils.isNotEmpty(pageSizeString)){
			maxLen = Integer.parseInt(pageSizeString);
		}
		int pageNumber = 1;
		String pageNumberString = request.getParameter("pageNumber");
		if (StringUtils.isNotEmpty(pageNumberString)){
			pageNumber = Integer.parseInt(pageNumberString);
		}

		Pager<MsgHistoryEntity> pager = new Pager<>(pageNumber,maxLen,"createDate", Pager.OrderType.desc);
		try {
			Pager<MsgHistoryEntity> listPage = chatService.getMsgHistoryPageList(forMemberId, toMemberId, pager);
			pager.setPageCount(listPage.getPageCount());
			pager.setTotalCount(listPage.getTotalCount());
			ResponseUtils.json(response, 200, listPage, null);
		}catch (Exception e){
			log.error("",e);
			ResponseUtils.json(response, 500,null, null);
		}
	}
}
