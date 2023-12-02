package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;


@Controller("SocketController")
@RequestMapping(value = "/socke")
@CrossOrigin
public class SocketController {

	
	@RequestMapping(value={"/println"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void println(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String txt = request.getParameter("txt");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   
	}
	 
	@RequestMapping(value={"/isExistByKey"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void isExistByKey(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String key = request.getParameter("key");
		
		List<WebSocketSession> ws = SessionStore.USERID_WS_MAP_GET_ByKey(key);
		if(null==ws||ws.size()<=0) {
			
			ResponseUtils.json(response, 200,"0" , null);
		} else {
			
			ResponseUtils.json(response, 200,"1" , null);
		}
	}
	

	@RequestMapping(value={"/isExistByUid"}, method={RequestMethod.POST,RequestMethod.OPTIONS})
	public void isExistByUid(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String uid = request.getParameter("uid");
		if(!SessionStore.isMemberOnline(uid)) {
			ResponseUtils.json(response, 200,"0" , null);
		} else {
			ResponseUtils.json(response, 200,"1" , null);
		}
	}
	
	

}
