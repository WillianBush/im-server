package com.imservices.im.bmm.web.controller;

import com.gexin.fastjson.JSON;
import com.imservices.im.bmm.service.RoomService;
import com.imservices.im.bmm.service.WebConfigService;
import com.imservices.im.bmm.service.push.UniPushService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Controller("ChatMsgController")
@RequestMapping(value = "/chat_msg")
@CrossOrigin
public class ChatMsgController {

	
	@Autowired
	private WebConfigService configService;
	@Autowired
	private RoomService roomService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private UniPushService uniPushService;
	@Autowired
	private SendUtils sendUtils;
	
	
	
//	@AuthPassport
	@RequestMapping(value={"/syncMsgData"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void syncMsgData(HttpServletRequest request, HttpServletResponse response) {
		try {
			String uid = request.getHeader("x-access-uid");
			if (uid.endsWith("#")){
				uid = uid.replace("#","");
			}
			if (uid.endsWith("#/")){
				uid = uid.replace("#/","");
			}
			String chatid = request.getParameter("chatid");

			log.info("syncMsgData::::::::uid+#+xAccessClient::{}",uid);
			log.info("syncMsgData::::::::chatid::{}",chatid);

			//设置分页
			HashMap<String,Object> pager=new HashMap<>();
			int pageCount=30;
			int totalCount=redisService.lLen(uid+"#"+chatid).intValue();
			pager.put("totalCount",totalCount);
			pager.put("pageCount",pageCount);
			pager.put("list", new ArrayList<>());
			int pageNumber = 1;
			String pageNumberString = request.getParameter("pageNumber");
			if (StringUtils.isNotEmpty(pageNumberString)){
				pageNumber = Integer.parseInt(pageNumberString);
			}
			pager.put("pageNumber",pageNumber+"");
			pager.put("pageSize",(int)Math.ceil((double)totalCount/30));

			List<String> list = redisService.lRange(uid+"#"+chatid, (pageNumber-1)*pageCount, pageNumber*pageCount>totalCount?totalCount:pageNumber*pageCount);
			if(null!=list&&list.size()>0) {
				Collections.reverse(list); //倒序排列
				List<Object> rsList = new ArrayList<>();
				list.forEach(msg -> {
					HashMap<String,Object> msgMap = JSON.parseObject(msg, HashMap.class);
					Object obj=msgMap.get("body");
					if(obj instanceof List) {
						rsList.add(msgMap.get("body"));
					}
				});
				pager.put("list", rsList);
				ResponseUtils.json(response, 200, pager, null);
			} else {
				//没缓存数据，用于前端把加载取消
				ResponseUtils.json(response,201,pager,null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("syncMsgData::{}",e);
		}
	}
}
