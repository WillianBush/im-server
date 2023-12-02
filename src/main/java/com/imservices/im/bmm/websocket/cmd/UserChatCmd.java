package com.imservices.im.bmm.websocket.cmd;

import com.google.common.collect.Lists;
import com.imservices.im.bmm.bean.*;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.entity.AccessRecord;
import com.imservices.im.bmm.entity.WaitSendMessage;
import com.imservices.im.bmm.entity.WebConfig;
import com.imservices.im.bmm.remote.MessageRemoteService;
import com.imservices.im.bmm.remote.RemoteMessage;
import com.imservices.im.bmm.service.AccessRecordService;
import com.imservices.im.bmm.service.ChatService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.WebConfigService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class UserChatCmd {
	 
    @Resource
	private AccessRecordService accessRecordService;
    @Resource
    private ChatService chatService;
    @Resource
    private MemberService memberService;
    @Resource
    private StoreComponent storeComponent;
    @Resource
	protected WebConfigService configService;
    @Resource
    private AccessRecordCmd accessRecordCmd;
    @Resource
	private  SendUtils sendUtils;
    @Resource
	private RedisService redisService;
	@Resource
	private RabbitTemplate rabbitTemplate;
	@Value("${server.port}")
	private String serverPort;
	@Resource
	private MessageRemoteService messageRemoteService;



	public void sengImag(ChatTxtBean bean,String imagePath)  throws Exception{
		String image =	"<img  style='max-width: 120px;max-height:120px;width:100%;' class='face' src='"+imagePath+"'>";
		bean.setTxt(image);
		bean.setPsr("uparse");
		this.sendTXT(bean);
	}
    
	public  void sendTXT(ChatTxtBean bean) throws Exception {
		//
			WebConfig wc = configService.get();
//			if(!"-1".equals(bean.getFromUid())) {
//				//
//				//不是官方团队则需要是否存在相关ws_session
//				if(null==FROM_WSSESSION) {
//					//
//					//如果自己的ws不存在,证明用户session过期没有了联系。则不需要后续操作,需要重新登陆才可以
//					return;
//				}
//			}  
			
			//
			SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
			//Member my = memberService.get(bean.getFromUid()); 
			MemberBean my = storeComponent.getMemberBeanFromMapDB(bean.getFromUid());
			if(null==my) {
				log.error("用户不存在,toUid:{}",bean.getToUid());
				throw new Exception("用户不存在");
			}
//			if(ChatStore.USER_BEAN_MAP.containsKey(bean.getFromUid())) {
//				my = ChatStore.USER_BEAN_MAP.get(bean.getFromUid());
//			} else {
//				throw new Exception("用户不存在");
//			}
			MemberBean to = storeComponent.getMemberBeanFromMapDB(bean.getToUid());
			if(null==to) {
				log.error("用户不存在,toUid:{}",bean.getToUid());
				throw new Exception("用户不存在");
			}
			//
			//Long l = memberService.blackListCount(new String[]{"mid","blacklist_ids"}, new Object[]{bean.getToUid(),"%"+bean.getFromUid()+"%"});
			String v = storeComponent.getBlackList(bean.getToUid());
			if(v.indexOf(bean.getFromUid())>=0) {
				log.error("您处于对方黑名单中，无法发送");
				throw new Exception("您处于对方黑名单中，无法发送");
			} else {
				//l = memberService.blackListCount(new String[]{"mid","blacklist_ids"}, new Object[]{bean.getFromUid(),"%"+bean.getToUid()+"%"});
				v = storeComponent.getBlackList(bean.getFromUid());
				if(v.indexOf(bean.getToUid())>=0) {
					log.error("您处于对方黑名单中，无法发送");
					throw new Exception("对方处于您的黑名单中，无法发送");
				}
			}
			//
			//不是官方团队则需要相关检查
			if(!"-1".equals(bean.getFromUid())) {
				if("-1".equals(bean.getToUid())) {
					log.error("禁止此类聊天");
					return;
				}
				//如果我方或对方不是超级用户，则需要进行好友检查
				if(StringUtils.isEmpty(wc.getSuperUser())||(wc.getSuperUser().indexOf(my.getMemberId())<0&&
						wc.getSuperUser().indexOf(to.getMemberId())<0)) {
					Long l = memberService.friendsCount(new String[]{"mid","friendid"}, new Object[]{bean.getFromUid(),bean.getToUid()});
					if(null==l||l<=0) {
						log.error("不是好友不能聊天");
						throw new Exception("不是好友不能聊天");
					}
					l = memberService.friendsCount(new String[]{"friendid","mid"}, new Object[]{bean.getFromUid(),bean.getToUid()});
					if(null==l||l<=0) {
						log.error("不是好友不能聊天");
						throw new Exception("不是好友不能聊天");
					}
				}
				
			}  
			
			//
			bean.setDate(sdf.format(new Date()));
			if(StringUtils.isEmpty(my.getHeadpic())) {
				bean.setFromHeadpic("/img_sys/defaultHeadPic.jpg");
			} else {
				bean.setFromHeadpic(my.getHeadpic());
			}
			bean.setDate(sdf.format(new Date()));
			bean.setFromName(my.getNickName());
			//
			synchronized (bean.getFromUid().intern()) {
				//发送给好友的信息
				MessageBean sendToUser = new MessageBean();
				sendToUser.setChatType("2");
				sendToUser.setChatid(bean.getFromUid());
				sendToUser.setType(MessageBean.MessageType.USER_TXT.name());
				sendToUser.setBean(bean);

				//把自己发的信息处理包装后并反馈给自己前端处理
				MessageBean sendFromUser = new MessageBean();
				sendFromUser.setChatType("2");
				sendFromUser.setChatid(bean.getToUid());
				sendFromUser.setType(MessageBean.MessageType.USER_TXT.name());
				sendFromUser.setBean(bean);


				//更新对方记录顺序
				List<AccessRecord> arlist = accessRecordService.getList(new String[]{"uid", "typeid", "entityid"}, new Object[]{bean.getToUid(), "2", bean.getFromUid()});
				AccessRecordBean arbean = null;
				if (null != arlist && !arlist.isEmpty()) {
					//
					AccessRecord ar = arlist.get(0);
					ar.setCreateDate(new Date());
					accessRecordService.update(ar);
//					arbean = BeanUtils.accessRecordToBean(ar);
//					accessRecordCmd.insertOrUpdate(arbean, bean.getToUid(), CMD_ENUM.AR_UPDATE);
				} else {
					//
					AccessRecord ar = new AccessRecord();
					ar.setCreateDate(new Date());
					ar.setUid(bean.getToUid());
					ar.setEntityid(bean.getFromUid());
					ar.setTypeid("2");
					//Member e = memberService.get(bean.getFromUid());
					if (StringUtils.isEmpty(my.getHeadpic())) {
						ar.setImg("/img_sys/defaultHeadPic.jpg");
					} else {
						ar.setImg(my.getHeadpic());
					}
					ar.setTitle(my.getNickName());
					accessRecordService.save(ar);
//					arbean = BeanUtils.accessRecordToBean(ar);
//					accessRecordCmd.insertOrUpdate(arbean, bean.getToUid(), CMD_ENUM.AR_INSERT);
				}

//			if(SessionStore.USERID_WS_MAP_containsKey(bean.getFromUid())) {
				//
				Message message = new Message();
				message.setBody(Lists.newArrayList(sendFromUser));
				message.setCMD(Message.CMD_ENUM.USER_CHAT_MESSAGE.name());
				log.info("sendRemoteSelf:将聊天消息存到数据库:{}",bean.getFromUid());
				sendUtils.send(bean.getFromUid(), message, chatService);
				/*** 将聊天消息存到数据库*/
//				rabbitTemplate.convertAndSend(RabbitmqConfig.BASIC_EXCHANGE,
//						RabbitmqConfig.MSG_HISTORY_ROUTING_KEY, JSON.toJSONString(bean));
//				saveMessageToFile(bean.getFromUid(),bean.getToUid(),sendFromUser);//把聊天信息缓存到文件
//			}  

				//如果不是发给官方团队 才能发出去。也就是说。发给官方团队的消息不需要发
				if (!"-1".equals(bean.getToUid())) {
					//判断要发送的对象是否在同一个服务器内
//					if(sendUtils.processRemoteUser(bean)) {
						Message message1 = new Message();
						message1.setBody(Lists.newArrayList(sendToUser));
						message1.setCMD(Message.CMD_ENUM.USER_CHAT_MESSAGE.name());
					log.info("sendRemoteSelf:将聊天消息存到数据库:{}",bean.getToUid());
						sendUtils.send(bean.getToUid(), message1, chatService);
//					}
					//其他服务器的 session 发送
//					messageRemoteService.requestRemoteSefMessage("im-cluster", RemoteMessage.builder()
//							.toUid(bean.getToUid())
//							.message(message)
//							.build()
//					);
				}
			}
	}

	/**
	 * 发给好友
	 * @param bean
	 * @throws Exception
	 */
	public  void sendTXTTo(ChatTxtBean bean) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
		MemberBean to = storeComponent.getMemberBeanFromMapDB(bean.getToUid());
		if(null==to) {
			log.error("用户不存在");
			throw new Exception("用户不存在");
		}

		bean.setDate(sdf.format(new Date()));

		synchronized (bean.getFromUid().intern()) {
			//发送给好友的信息
			MessageBean sendToUser = new MessageBean();
			sendToUser.setChatType("2");
			sendToUser.setChatid(bean.getFromUid());
			sendToUser.setType(MessageBean.MessageType.USER_TXT.name());
			sendToUser.setBean(bean);

			//如果不是发给官方团队 才能发出去。也就是说。发给官方团队的消息不需要发
			if (!"-1".equals(bean.getToUid())) {
				Message message1 = new Message();
				message1.setBody(Lists.newArrayList(sendToUser));
				message1.setCMD(Message.CMD_ENUM.USER_CHAT_MESSAGE.name());
				sendUtils.send(bean.getToUid(), message1, chatService);
			}
		}
	}

	public  void sendTXTToByMq(ChatTxtBean bean) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
		MemberBean to = storeComponent.getMemberBeanFromMapDB(bean.getToUid());
		if(null==to) {
			log.error("用户不存在");
			throw new Exception("用户不存在");
		}

		bean.setDate(sdf.format(new Date()));

		synchronized (bean.getFromUid().intern()) {
			//发送给好友的信息
			MessageBean sendToUser = new MessageBean();
			sendToUser.setChatType("2");
			sendToUser.setChatid(bean.getFromUid());
			sendToUser.setType(MessageBean.MessageType.USER_TXT.name());
			sendToUser.setBean(bean);

			//如果不是发给官方团队 才能发出去。也就是说。发给官方团队的消息不需要发
			if (!"-1".equals(bean.getToUid())) {
				Message message1 = new Message();
				message1.setBody(Lists.newArrayList(sendToUser));
				message1.setCMD(Message.CMD_ENUM.USER_CHAT_MESSAGE.name());
				sendUtils.sendByMQ(bean.getToUid(), message1, chatService);
			}
		}
	}
	

	public void login(WebSocketSession arg0) throws Exception {
		Message message = new Message();
		message.setBody(""); 
		message.setCMD(Message.CMD_ENUM.LOGIN.name());
		sendUtils.send(arg0,message);
	}

	public void msgUndo(ChatTxtBean bean) throws Exception {

		List<WebSocketSession> TO_WSSESSION_list = SessionStore.USERID_WS_MAP_GET_ByUid(bean.getToUid());
		List<WebSocketSession> FROM_WSSESSION_list = SessionStore.USERID_WS_MAP_GET_ByUid(bean.getFromUid());

		if (!"-1".equals(bean.getToUid())) {
			MessageBean msg = new MessageBean();
			msg.setChatid(bean.getFromUid());
			msg.setBean(bean);
			Message message = new Message();
			message.setBody(msg);
			message.setCMD(Message.CMD_ENUM.CHAT_MSG_UNDO.name());
			for (WebSocketSession TO_WSSESSION : TO_WSSESSION_list) {
				if (null != TO_WSSESSION) {
					sendUtils.send(TO_WSSESSION, message);
				} else {
					//如果对方不在线，则把信息保存到数据库，等他上线后发送
					WaitSendMessage wsm = new WaitSendMessage();
					wsm.setUuid(bean.getUuid());
					wsm.setSimple_content(bean.getSimple_content());
					wsm.setCmd(Message.CMD_ENUM.CHAT_MSG_UNDO.name());
					wsm.setChatid(bean.getFromUid());
					wsm.setContent(bean.getTxt());
					wsm.setName(bean.getFromName());
					wsm.setFromUid(bean.getFromUid());
					wsm.setToUid(bean.getToUid());
					chatService.saveWSM(wsm);
				}
			}
			messageRemoteService.requestRemoteSefMessage("im-cluster", RemoteMessage.builder()
					.message(message)
					.toUid(bean.getFromUid())
					.CMD(message.getCMD())
					.build());
		}

		MessageBean msg = new MessageBean();
		msg.setChatid(bean.getToUid());
		msg.setBean(bean);
		Message message = new Message();
		message.setBody(msg);
		message.setCMD(Message.CMD_ENUM.CHAT_MSG_UNDO.name());
		for (WebSocketSession FROM_WSSESSION : FROM_WSSESSION_list) {
			if (null != FROM_WSSESSION) {
				sendUtils.send(FROM_WSSESSION, message);
			}
		}
		messageRemoteService.requestRemoteSefMessage("im-cluster", RemoteMessage.builder()
				.message(message)
				.toUid(bean.getToUid())
				.CMD(message.getCMD())
				.build());
	}  




	public void sendCard(ChatCardBean bean) throws Exception {
		WebConfig wc = configService.get();
		List<WebSocketSession> TO_WSSESSION_list = SessionStore.USERID_WS_MAP_GET_ByUid(bean.getToUid());
		List<WebSocketSession> FROM_WSSESSION_list = SessionStore.USERID_WS_MAP_GET_ByUid(bean.getFromUid());
		
		
//		if(!"-1".equals(bean.getFromUid())) {
//			//不是官方团队则需要是否存在相关ws_session
//			if(null==FROM_WSSESSION) {
//				//如果自己的ws不存在,证明用户session过期没有了联系。则不需要后续操作,需要重新登陆才可以
//				return;
//			}
//		}  
		
		//
		
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
		//Member my = memberService.get(bean.getFromUid());
		MemberBean my =  storeComponent.getMemberBeanFromMapDB(bean.getFromUid());
		if(null==my) {
			throw new Exception("用户不存在");
		}
		MemberBean to = storeComponent.getMemberBeanFromMapDB(bean.getToUid());
		
		
		//Long l = memberService.blackListCount(new String[]{"mid","blacklist_ids"}, new Object[]{bean.getToUid(),"%"+bean.getFromUid()+"%"});
		String v = storeComponent.getBlackList(bean.getToUid());
		if(v.indexOf(bean.getFromUid())>=0) {
			throw new Exception("您处于对方黑名单中，无法发送");
		} else {
			//l = memberService.blackListCount(new String[]{"mid","blacklist_ids"}, new Object[]{bean.getFromUid(),"%"+bean.getToUid()+"%"});
			v = storeComponent.getBlackList(bean.getFromUid());
			if(v.indexOf(bean.getToUid())>=0) {
				throw new Exception("对方处于您的黑名单中，无法发送");
			}
		}
		
		if(!"-1".equals(bean.getFromUid())) {
			if("-1".equals(bean.getToUid())) {
				throw new Exception("禁止此类聊天");
			}
			//不是官方团队则需要相关检查
			//如果我方或对方不是超级用户，则需要进行好友检查
			if(StringUtils.isEmpty(wc.getSuperUser())||(wc.getSuperUser().indexOf(my.getMemberId())<0&&
					wc.getSuperUser().indexOf(to.getMemberId())<0)) {
				Long l = memberService.friendsCount(new String[]{"mid","friendid"}, new Object[]{bean.getFromUid(),bean.getToUid()});
				if(null==l||l<=0) {
					throw new Exception("不是好友不能聊天");
				}
				l = memberService.friendsCount(new String[]{"friendid","mid"}, new Object[]{bean.getFromUid(),bean.getToUid()});
				if(null==l||l<=0) {
					throw new Exception("不是好友不能聊天");
				}
			}
			
		}
		//
		
		bean.setDate(sdf.format(new Date()));
		if(StringUtils.isEmpty(my.getHeadpic())) {
			bean.setFromHeadpic("/img_sys/defaultHeadPic.jpg");
		} else {
			bean.setFromHeadpic(my.getHeadpic());
		}
		bean.setDate(sdf.format(new Date()));
		bean.setFromName(my.getNickName());
		
		//发送给好友的信息
		MessageBean sendToUser = new MessageBean();
		sendToUser.setChatType("2");
		sendToUser.setChatid(bean.getFromUid());
		sendToUser.setType(MessageBean.MessageType.USER_CARD.name()); 
		sendToUser.setBean(bean);
		
		//把自己发的信息处理包装后并反馈给自己前端处理
		MessageBean sendFromUser = new MessageBean();
		sendFromUser.setChatType("2");
		sendFromUser.setChatid(bean.getToUid());
		sendFromUser.setType(MessageBean.MessageType.USER_CARD.name()); 
		sendFromUser.setBean(bean);
		
		
		
		//更新对方记录顺序
		List<AccessRecord> arlist = accessRecordService.getList(new String[]{"uid","typeid","entityid"}, new Object[]{bean.getToUid(),"2",bean.getFromUid()});
		AccessRecordBean arbean = null;
		if(null!=arlist&&!arlist.isEmpty()) {
			AccessRecord ar = arlist.get(0);
			ar.setCreateDate(new Date());
			accessRecordService.update(ar);
//			arbean = BeanUtils.accessRecordToBean(ar);
//			accessRecordCmd.insertOrUpdate(arbean, bean.getToUid(), CMD_ENUM.AR_UPDATE);
		} else {
			AccessRecord ar = new AccessRecord();
			ar.setCreateDate(new Date());
			ar.setUid(bean.getToUid());
			ar.setEntityid(bean.getFromUid());
			ar.setTypeid("2");
			//Member e = memberService.get(bean.getFromUid());
			if(StringUtils.isEmpty(my.getHeadpic())) { 
				ar.setImg("/img_sys/defaultHeadPic.jpg");
			} else {
				ar.setImg(my.getHeadpic());
			}
			ar.setTitle(my.getNickName());
			accessRecordService.save(ar);
//			arbean = BeanUtils.accessRecordToBean(ar);
//			accessRecordCmd.insertOrUpdate(arbean, bean.getToUid(), CMD_ENUM.AR_INSERT);
		}
		 
	
		for(WebSocketSession FROM_WSSESSION :FROM_WSSESSION_list) {
			if(null!=FROM_WSSESSION) {
				Message message = new Message();
				message.setBody(Lists.newArrayList(sendFromUser)); 
				message.setCMD(Message.CMD_ENUM.USER_CHAT_MESSAGE.name());
				sendUtils.send(FROM_WSSESSION,message);
//				saveMessageToFile(bean.getFromUid(),bean.getToUid(),sendFromUser);//把聊天信息缓存到文件
			}
		}
		
		boolean flag = true;  
		for(WebSocketSession TO_WSSESSION :TO_WSSESSION_list) {
			if(!"-1".equals(bean.getToUid())) {
				if(null!=TO_WSSESSION) {  
					//
					Message message = new Message();
					message.setBody(Lists.newArrayList(sendToUser));
					message.setCMD(Message.CMD_ENUM.USER_CHAT_MESSAGE.name());
					sendUtils.send(TO_WSSESSION,message);
					flag = false;
//					saveMessageToFile(bean.getToUid(),bean.getFromUid(),sendToUser);//把聊天信息缓存到文件
				}
			}
		}
		//如果不是发给官方团队 才能发出去。也就是说。发给官方团队的消息不需要发
		if(!"-1".equals(bean.getToUid())) {
			if(flag) {
				//
				//如果对方不在线，则把信息保存到数据库，等他上线后发送
				WaitSendMessage wsm = new WaitSendMessage();
				wsm.setUuid(bean.getUuid());
				wsm.setSimple_content(bean.getSimple_content());
				wsm.setChatid(sendToUser.getChatid());
				wsm.setCreateDate(new Date());
				wsm.setDate(bean.getDate());   
				wsm.setFromUid(bean.getFromUid());
				wsm.setHeadpic(bean.getFromHeadpic());
				wsm.setName(bean.getFromName());
				wsm.setToUid(bean.getToUid());
				wsm.setType(sendToUser.getType());
				wsm.setMheadpic(bean.getMheadpic());
				wsm.setMid(bean.getMid());
				wsm.setMname(bean.getMname());
				wsm.setMuuid(bean.getMuuid());
				chatService.saveWSM(wsm);   
			}
		}
		
	}


}
