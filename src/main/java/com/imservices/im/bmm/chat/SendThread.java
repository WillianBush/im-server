package com.imservices.im.bmm.chat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.bean.ChatBaseBean;
import com.imservices.im.bmm.bean.ChatCardBean;
import com.imservices.im.bmm.bean.ChatTxtBean;
import com.imservices.im.bmm.bean.MessageBean;
import com.imservices.im.bmm.bean.store.ChatStore;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.service.ChatService;
import com.imservices.im.bmm.utils.JsonUtil;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Message.CMD_ENUM;
import com.imservices.im.bmm.websocket.Message.MessageAct;
import com.imservices.im.bmm.websocket.core.WsSessionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class SendThread  {

	private RedisService redisService;
	private ChatService chatService;
	private RabbitTemplate rabbitTemplate;


	private WsSessionService wsSessionService;

//	@Autowired
//	public SendThread(RedisService redisService) {
//		this.redisService = redisService;
//	}

	@Async("taskExecutor")
	public void run(WebSocketSession session,Message message) throws Exception {
		try {
			ChatConfig.chat_send_semaphore.acquire();//申请资源
			if (session == null ){
				return;
			}
			if (!session.isOpen()){
				wsSessionService.removeSession(session);
				session.close();
				return;
			}

			final String key = SessionStore.WS_USERID_MAP.get(session);
			if(null != session) {
					try {
						synchronized (session) {
							session.sendMessage(new TextMessage(JsonUtil.getJSONString(message)));
						}
						if(!StringUtils.isEmpty(key)) {
							String uid = key.split("#")[0];
							Message message1 = message.clone();
							if(
									CMD_ENUM.USER_CHAT_MESSAGE.name().equals(message1.getCMD())||
											CMD_ENUM.GROUP_CHAT_MESSAGE.name().equals(message1.getCMD())||
											CMD_ENUM.CHAT_SYS_TXT.name().equals(message1.getCMD())||
											CMD_ENUM.CHAT_SEND_RED_SUCCESS.name().equals(message1.getCMD())||
											CMD_ENUM.CHAT_SEND_TRANSFER_SUCCESS.name().equals(message1.getCMD())||
											CMD_ENUM.CHAT_MSG_READED.name().equals(message1.getCMD())||
											CMD_ENUM.CHAT_TRANSFER_FINISHED.name().equals(message1.getCMD())||
											CMD_ENUM.CHAT_TRANSFER_EXPIRED.name().equals(message1.getCMD())||
											CMD_ENUM.CHAT_RED_EXPIRED.name().equals(message1.getCMD())||
											CMD_ENUM.CHAT_MSG_UNDO.name().equals(message1.getCMD())
							) {

								String chatid = "";
								String fromuid = "";
								String msgMgr = "";
								boolean isRoom = false;
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
								if(CMD_ENUM.USER_CHAT_MESSAGE.name().equals(message1.getCMD())||
										CMD_ENUM.GROUP_CHAT_MESSAGE.name().equals(message1.getCMD())||
										CMD_ENUM.CHAT_SYS_TXT.name().equals(message1.getCMD())
//										CMD_ENUM.CHAT_SEND_RED_SUCCESS.name().equals(message1.getCMD())||
//										CMD_ENUM.CHAT_SEND_TRANSFER_SUCCESS.name().equals(message1.getCMD())
									) {
									List<MessageBean> list = null;
									try{
										list = JSONArray.parseArray(JSONObject.toJSONString(message1.getBody()),MessageBean.class);
									}catch (Exception e){
										log.error("message1.getBody():{}",message1.getBody().toString(),e);
										return;
									}
									chatid = list.get(0).getChatid();
									ChatBaseBean bean_1 = JSONObject.parseObject(JSONObject.toJSONString(list.get(0).getBean()),ChatBaseBean.class);
									fromuid = bean_1.getFromUid();
//									/**
//									 * 只存入发送者的消息
//									 */
//									if(uid.equals(fromuid)) {
//										rabbitTemplate.convertAndSend(RabbitmqConfig.BASIC_EXCHANGE,
//												RabbitmqConfig.MSG_HISTORY_ROUTING_KEY, JSON.toJSONString(list.get(0).getBean()));
//									}
									if(CMD_ENUM.USER_CHAT_MESSAGE.name().equals(message1.getCMD())||
											CMD_ENUM.GROUP_CHAT_MESSAGE.name().equals(message1.getCMD())
									) {

										Object obj = list.get(0).getBean();
										if(obj instanceof ChatTxtBean) {
											ChatTxtBean bean =  (ChatTxtBean) list.get(0).getBean();
											if(!StringUtils.isEmpty(bean.getToGroupid())) {
												isRoom = true;
											}
											msgMgr+=("<div class='dateStr'>"+sdf.format(new Date())+" From【"+bean.getFromName()+"】</div>");
											msgMgr += ("<div class='txtDiv'><div class='txtStr'>"+bean.getTxt()+"</div></div>");
										} else if(obj instanceof ChatCardBean) {
											ChatCardBean bean =  (ChatCardBean) list.get(0).getBean();
											if(!StringUtils.isEmpty(bean.getToGroupid())) {
												isRoom = true;
											}
											msgMgr+=("<div class='dateStr'>"+sdf.format(new Date())+" From【"+bean.getFromName()+"】</div>");
											msgMgr += ("<div class='txtDiv'><div class='txtStr'>[名片]</div></div>");
										}
									}
								}


								if(
										CMD_ENUM.CHAT_MSG_READED.name().equals(message1.getCMD())
								) {
									chatid = message1.getBody().toString();
								}

								if(
										CMD_ENUM.CHAT_TRANSFER_FINISHED.name().equals(message1.getCMD())||
												CMD_ENUM.CHAT_RED_EXPIRED.name().equals(message1.getCMD())||
												CMD_ENUM.CHAT_TRANSFER_EXPIRED.name().equals(message1.getCMD())
								) {
									ChatBaseBean bean = (ChatBaseBean) message1.getBody();
									chatid = bean.getChatid();
								}
								if(
										CMD_ENUM.CHAT_MSG_UNDO.name().equals(message1.getCMD())
								) {
									MessageBean bean = (MessageBean) message1.getBody();
									chatid = bean.getChatid();
								}
								message1.setAct(MessageAct.cloudStorageData.name());

								//清理重复数据，多端状态下会有重复
								Long rs=redisService.lRemove(uid+"#"+chatid,0,JsonUtil.getJSONString(message1));
								log.info("清理重复数据，多端状态下会有重复:{}",rs);
								redisService.lLeftPush(uid+"#"+chatid, JsonUtil.getJSONString(message1));
								//用户云端数据存储
								//受客户端清除数据影响
								cloudStore(uid,chatid,isRoom,false);


								if(isRoom) {
									if(!StringUtils.isEmpty(fromuid)&&fromuid.equals(uid)) {
										//因为考虑到用户可以清除群记录。所以前端信息保存的时候 需要用户#群ID
										//后台查询群信息时，则不需要，所以这里直接群ID就可以了
										redisService.lLeftPush(chatid+"#mgr", msgMgr);
										//管理云端数据存储
										//不受客户端清除数据影响
										cloudStore(uid,chatid,isRoom,true);
									}
								} else {
									redisService.lLeftPush(uid+"#"+chatid+"#mgr", msgMgr);
									//管理云端数据存储
									//不受客户端清除数据影响
									cloudStore(uid,chatid,isRoom,true);
								}
							}
						}
					} catch (Exception e) {
						log.error("发送异常",e);
						log.error("session_id:{}", session.getId());
						log.error("session_isOpen:{}", session.isOpen());
						wsSessionService.removeSession(session);
						if(!CMD_ENUM.PING.toString().equals(message.getCMD())&&!CMD_ENUM.LOGIN_USER_REMOVE.toString().equals(message.getCMD())) {
							if(!StringUtils.isEmpty(key)) {
								if(ChatStore.WAIT_SEND_MESSAGE.containsKey(key)) {
									ChatStore.WAIT_SEND_MESSAGE.get(key).add(message);
								} else {
									List<Message> list = new ArrayList<Message>();
									list.add(message);
									ChatStore.WAIT_SEND_MESSAGE.put(key,list);
								}
							}
						}
						ChatConfig.chat_send_semaphore.release();
						session.close();
					}
			}
		} catch (Exception e) {
			log.error("SendThread",e);
		} finally {
			//释放资源
			ChatConfig.chat_send_semaphore.release();
		}

	}


	//云端数据储存
	public void cloudStore(String uid,String chatid,boolean isRoom,boolean isMgr) {
		String suffix = isMgr?"#mgr":"";
		String prefix = uid+"#"+chatid;
		if(isRoom&&isMgr) {
			prefix = chatid;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		if(redisService.hasKey(prefix+"_dateStr"+suffix)) {
			String str = redisService.get(prefix+"_dateStr"+suffix);
			String[] arrs = str.split(",");
			int keepDays = ChatConfig.cloudUserChatDataKeepDays;
			if(isRoom) {
				keepDays = ChatConfig.cloudGroupChatDataKeepDays;
			}
			if(keepDays<=0) keepDays = 99999;//如果为0，则为不限制
			if(str.indexOf(sdf.format(new Date()))<0) {
				//超过数据保存天数，则清除
				if(arrs.length>=keepDays) {
					String countStr = redisService.get(prefix+"_"+arrs[0]+"_count"+suffix);
					if (countStr == null) {
						return;
					}
					redisService.delete(prefix+"_"+arrs[0]+"_count"+suffix);
					arrs = ArrayUtils.remove(arrs, 0);
					for(int i = 0;i<Integer.valueOf(countStr);i++) {
						redisService.lRightPop(prefix+suffix);
					}
				}
				ArrayUtils.add(arrs, sdf.format(new Date()));
				redisService.set(prefix+"_dateStr"+suffix, ArrayUtils.toString(arrs).replaceAll("\\{", "").replaceAll("\\}", ""));
				redisService.set(prefix+"_"+sdf.format(new Date())+"_count"+suffix, "1");
			} else {
				String countStr = redisService.get(prefix+"_"+sdf.format(new Date())+"_count"+suffix);
				redisService.set(prefix+"_"+sdf.format(new Date())+"_count"+suffix, Integer.valueOf(countStr)+1+"");
			}

		} else {
			redisService.set(prefix+"_dateStr"+suffix, sdf.format(new Date()));
			redisService.set(prefix+"_"+sdf.format(new Date())+"_count"+suffix, "1");
		}
	}
}

