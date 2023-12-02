package com.imservices.im.bmm.websocket.cmd;

import com.google.common.collect.Lists;
import com.imservices.im.bmm.bean.AccessRecordBean;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Message.CMD_ENUM;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class AccessRecordCmd {

	private  SendUtils sendUtils;
	
	public  void insertOrUpdate(final AccessRecordBean bean,final String toUID,final CMD_ENUM cmdType) {
		//最好是停顿一下，特别是对于刚注册还没来得及到消息页面的时候导致无法更新列表，小秘书没有
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000L);
					Message message = new Message();
					message.setBody(Lists.newArrayList(bean));
					message.setCMD(cmdType.name());
					log.info("AccessRecordCmd============");
					sendUtils.send(toUID,message);
				} catch (Exception e) {
					log.error("",e);
				}
			}
		}).start();
		
		
	} 

}
