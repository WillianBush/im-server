package com.imservices.im.bmm.websocket.cmd;

import com.imservices.im.bmm.bean.FriendsAddBean;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
public class FriendsCmd {
	
	@Autowired
	private  SendUtils sendUtils;
	public  void sendFriendsAdd(FriendsAddBean bean,String toUID) throws Exception {
		List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(toUID);
		for(WebSocketSession ws : ws_list) {
			Message message = new Message();
			message.setBody(Lists.newArrayList(bean));
			message.setCMD(Message.CMD_ENUM.FRIENDSADD.name());
			sendUtils.send(ws,message);
		}
		
	} 


}
 