package com.imservices.im.bmm.web.listener;

import com.imservices.im.bmm.constant.MemberConstant;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener,HttpSessionAttributeListener  {

	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
//		if(arg0.getName().equals(MemberConstant.MEMBERIDSESSION)) {
//			String UID = (String) arg0.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
//			SessionStore.USER_SESSIONID_MAP.put(UID, arg0.getSession().getId());
//		}
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
	}
   
	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		
	}

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
	}

	@Override  
	public void sessionDestroyed(HttpSessionEvent arg0) {
		
		String UID = (String) arg0.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
		String xAccessClient = (String) arg0.getSession().getAttribute("x-access-client");
		
//		if(!StringUtils.isEmpty(UID)) {
//			
//			WebSocketSession ws = SessionStore.USERID_WS_MAP_GET_ByKey(UID+"#"+xAccessClient);
//			if(null!=ws) SessionStore.WS_USERID_MAP.remove(ws);
//			SessionStore.USERID_WS_MAP_REMOVE(UID+"#"+xAccessClient);  
//		}  
	}



}
