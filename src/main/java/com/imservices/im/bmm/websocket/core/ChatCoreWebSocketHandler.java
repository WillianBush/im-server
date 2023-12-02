package com.imservices.im.bmm.websocket.core;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.imservices.im.bmm.bean.*;
import com.imservices.im.bmm.bean.store.ChatStore;
import com.imservices.im.bmm.bean.store.ChatStoreComponent;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.entity.*;
import com.imservices.im.bmm.mq.RabbitmqConfig;
import com.imservices.im.bmm.service.AccessRecordService;
import com.imservices.im.bmm.service.ChatService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.service.WebConfigService;
import com.imservices.im.bmm.service.push.UniPushService;
import com.imservices.im.bmm.utils.FTPUtil;
import com.imservices.im.bmm.utils.GroupMethod;
import com.imservices.im.bmm.utils.HtmlUtils;
import com.imservices.im.bmm.utils.JsonUtil;
import com.imservices.im.bmm.utils.oss.OSSModel;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.spring.PropertiesConfig;
import com.imservices.im.bmm.utils.web.BeanUtils;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Message.CMD_ENUM;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import com.imservices.im.bmm.websocket.WebSocketSessionDto;
import com.imservices.im.bmm.websocket.cmd.GroupChatCmd;
import com.imservices.im.bmm.websocket.cmd.UserChatCmd;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ChatCoreWebSocketHandler extends TextWebSocketHandler {

    public static Map<String, WebSocketSession> appuuid_ws = new HashMap<String, WebSocketSession>();

    public static  Map<Thread,String> currentSessionId = new HashMap<>();

    @Resource
    private UserChatCmd userChatCmd;
    @Resource
    private GroupChatCmd groupChatCmd;
    @Resource
    private ChatService chatService;
    @Resource
    private MemberService memberService;
    @Resource
    private StoreComponent storeComponent;
    @Resource
    private WebConfigService configService;
    @Resource
    private AccessRecordService arService;

    @Autowired
    private UniPushService uniPushService;

    @Resource
    private SendUtils sendUtils;

    @Resource
    private RedisService redisService;

    @Resource
    private Environment environment;

    @Resource
    private OSSModel ossModel;
    @Resource
    private ChatStoreComponent chatStoreComponent;

    @Resource
    private WsSessionService wsSessionService;


    @Override
    public void afterConnectionClosed(WebSocketSession arg0, CloseStatus arg1)
            throws Exception {
        try {
            wsSessionService.removeSession(arg0);
        }catch (Exception e){

        }
        arg0.close();

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession arg0)
            throws Exception {
        synchronized (this) {
            WebSocketSessionDto webSocketSession = new WebSocketSessionDto(arg0);
            if (appuuid_ws.containsKey(webSocketSession.getAppUUid())) {
                appuuid_ws.get(webSocketSession.getAppUUid()).close();
                appuuid_ws.remove(webSocketSession.getAppUUid());
            }
            appuuid_ws.put(webSocketSession.getAppUUid(), arg0);
        }
    }


    @Override
    public void handleMessage(WebSocketSession arg0, WebSocketMessage<?> arg1) {
        try {
            String[] pls = arg1.getPayload().toString().split("=!@#&=");
            Message message = null;
            WebSocketSessionDto webSocketSession = new WebSocketSessionDto(arg0);
            currentSessionId.put(Thread.currentThread(),webSocketSession.getSessionId());
            try {
                message = (Message) JsonUtil.getDTO(pls[0], Message.class);
            } catch (Exception e) {
                log.error("pls[0]:{}",pls[0], e);
                return;
            }
            if (null == message) return;

            if (message.getCMD().equals(Message.CMD_ENUM.PING.name())) {
                Message msg = new Message();
                msg.setCMD(CMD_ENUM.PING.toString());
                msg.setBody(null);
                sendUtils.send(arg0, msg);
            } else if (message.getCMD().equals(Message.CMD_ENUM.APP_HIDE_SHOW.name())) {
                Map map = JsonUtil.getMapFromJson(message.getBody().toString());
                if (ChatStore.USER_APPPUSH.containsKey(map.get("uid").toString())) {
                    AppPush ap = ChatStore.USER_APPPUSH.get(map.get("uid").toString());

                    Integer status = Integer.valueOf(map.get("status").toString());
                    if (0 == status) {
                        //后台
                        ap.setAppHide(true);
                    } else if (1 == status) {
                        //前台
                        ap.setAppHide(false);
                    }
                }
            } else if (message.getCMD().equals(Message.CMD_ENUM.PUTSESSION.name())) {
//                log.info("message:{}",JSONObject.toJSONString(message));
                synchronized (Message.CMD_ENUM.PUTSESSION) {

                    WebConfig wc = configService.get();
                    //
                    //添加session联系
                    //如果之前有登陆过了，则退出登陆过的
					Map map ;
					try {
						map = JsonUtil.getMapFromJson(message.getBody().toString());
					}catch (Exception e) {
						log.error("添加session联系,错误,message:{}", JSONObject.toJSONString(message),e);
						return;
					}


                    //检查是否冲线,特权用户无需检查
                    chongxian_check(map.get("user_id").toString(), map.get("app_uuid").toString());
//                    log.info("putsession");
                    /**统一设置缓存信息*/
                    putSessionInitCache(map,arg0);

                    if (redisService.hasKey(map.get("user_id").toString() + "#clearChatMsgData")) {
                        String val = redisService.get(map.get("user_id").toString() + "#clearChatMsgData");
                        Message msg = new Message();
                        msg.setCMD(CMD_ENUM.CLEAR_CHAT_MSG_DATA_MGR.toString());
                        msg.setBody(val);
                        sendUtils.send(arg0, msg);
                        redisService.delete(map.get("user_id").toString() + "#clearChatMsgData");
                    }
                    reSend(map.get("user_id").toString(), arg0);
                    if (ObjectUtils.isEmpty(chatStoreComponent.getMemberBean(map.get("user_id").toString().split("#")[0]))) {
                        //如果用户缓存不存在，针对页面刷新不需要登陆缓存找不到的情况
                        Member member = memberService.get(map.get("user_id").toString().split("#")[0]);
                        if (null == member) {//针对登陆已删除的用户
                            log.error("用户不存在：{}",map.get("user_id").toString().split("#")[0]);
                            Message msg = new Message();
                            msg.setCMD(CMD_ENUM.LOGIN_USER_REMOVE.toString());
                            msg.setBody(null);
                            sendUtils.send(arg0, msg);
                            // todo
//                            SessionStore.USERID_WS_MAP_REMOVE(map.get("user_id").toString());
                            SessionStore.USERID_SESSION_WS_MAP.remove(map.get("user_id").toString());
                            SessionStore.WS_USERID_MAP.remove(arg0);
                            /**将缓存在redis的请求地址删除**/
                            redisService.hDelete(SessionStore.REDIS_WSS_KEY,map.get("user_id").toString().split("#")[0]);
                            redisService.hDelete(SessionStore.ONLINE_MEMBER,map.get("user_id").toString());
                            return;
                        }
                        chatStoreComponent.putMemberBean(map.get("user_id").toString().split("#")[0], BeanUtils.memberToBeanSimple(member));

                        //如果是超级用户即记录在线
                        if (!StringUtils.isEmpty(wc.getSuperUser()) && wc.getSuperUser().indexOf(member.getMemberId()) >= 0) {
                            ChatStore.ONLINE_SUPER_USER.put(member.getId(), "");
                        }
                    }
                    if (!ChatStore.BLACK_LIST.containsKey(map.get("user_id").toString().split("#")[0])) {
                        List<Blacklist> list = memberService.getBlackList(new String[]{"mid"}, new Object[]{map.get("user_id").toString().split("#")[0]});
                        if (!list.isEmpty()) {
                            Blacklist o = list.get(0);
                            ChatStore.BLACK_LIST.put(map.get("user_id").toString().split("#")[0], o.getBlacklist_ids());
                        } else {
                            ChatStore.BLACK_LIST.put(map.get("user_id").toString().split("#")[0], "");
                        }
                    }
                    //上线通知，我上线了
                    try {
                        List<AccessRecord> arlist = arService.getList(new String[]{"entityid"}, new Object[]{map.get("user_id").toString().split("#")[0]});
                        if (!arlist.isEmpty()) {
                            for (AccessRecord o : arlist) {
                                if(!ObjectUtils.isEmpty(chatStoreComponent.getMemberBean(o.getUid()))){
                                    List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(o.getUid());
                                    for (WebSocketSession ws : ws_list) {
                                        Message msg = new Message();
                                        msg.setCMD(CMD_ENUM.FRIEND_ONLINE.toString());
                                        msg.setBody(o.getEntityid());
                                        sendUtils.send(ws, msg);
                                    }

                                }
                            }
                        }
                    }catch (Exception e){
                        log.error("上线通知，我上线了,错误",e);
                    }


                    //如果我在某个超级用户聊天聊天中，则通知对方我的状态
                    Iterator<String> it = ChatStore.ONLINE_SUPER_USER.keySet().iterator();
                    while (it.hasNext()) {
                        String super_user_id = it.next();
                        Long l = arService.count(new String[]{"uid", "entityid"}, new Object[]{super_user_id, map.get("user_id").toString().split("#")[0]});
                        if (null != l && l > 0) {
                            Message msg = new Message();
                            msg.setCMD(CMD_ENUM.FRIEND_ONLINE.toString());
                            msg.setBody(map.get("user_id").toString().split("#")[0]);
                            sendUtils.send(super_user_id, msg);
                        }
                    }
                }
            } else if (message.getCMD().equals(Message.CMD_ENUM.CHAT_SEND_CARD.name())) {
                ChatCardBean bean = (ChatCardBean) JsonUtil.getDTO(message.getBody().toString(), ChatCardBean.class);
                MemberBean mb = storeComponent.getMemberBeanFromMapDB(bean.getMuuid());
                bean.setMheadpic(mb.getHeadpic());
                bean.setMid(mb.getMemberId());
                bean.setMname(mb.getNickName());
                bean.setSimple_content("[名片]");
                if (!StringUtils.isEmpty(bean.getToGroupid())) {
                    groupChatCmd.sendCard(bean);
                } else {
                    userChatCmd.sendCard(bean);

                    if (ChatStore.USER_APPPUSH.containsKey(bean.getToUid())) {
                        AppPush ap = ChatStore.USER_APPPUSH.get(bean.getToUid());

                        PushMessage pm = new PushMessage();
                        pm.setTitle(bean.getFromName());
                        pm.setContent("[名片]");
                        pm.setUserRole("");
                    }
                }
            } else if (message.getCMD().equals(Message.CMD_ENUM.USER_CHAT_SEND_TXT.name())
                    || message.getCMD().equals(Message.CMD_ENUM.GROUP_CHAT_SEND_TXT.name())) {
                log.info("接收到的消息:{}",JSONObject.toJSONString(message));
                WebConfig wc = configService.get();
                //
                ChatTxtBean bean = (ChatTxtBean) JsonUtil.getDTO(message.getBody().toString(), ChatTxtBean.class);

                bean.setOldTxt(bean.getTxt());
                if (StringUtils.isEmpty(bean.getUuid())) {
                    bean.setUuid("a" + UUID.randomUUID().toString().replaceAll("-", ""));
                }

                //[f1#12]即为/img/emotion/face01/12.png
                //
                String txt = bean.getTxt();
                if (txt.contains("/" + FTPUtil.FTP_FOLDER.chat_video.name()) || txt.toLowerCase().endsWith(".mp4")|| txt.toLowerCase().endsWith(".webm")|| txt.toLowerCase().endsWith(".ogg")|| txt.toLowerCase().endsWith(".wma")|| txt.toLowerCase().endsWith(".rmvb")|| txt.toLowerCase().endsWith(".rm")|| txt.toLowerCase().endsWith(".avi")) {
                    bean.setPsr("video");
                    bean.setSimple_content("[视频]");
                    log.info("[视频]"+txt);
                    bean.setTxt( "<video   src='"+ ossModel.getDomain() + txt +"' />");
                } else if (txt.contains("/" + FTPUtil.FTP_FOLDER.chat_voice.name()) || txt.toLowerCase().endsWith(".mp3")) {
                    bean.setSimple_content("[语音]");
                    bean.setPsr("voice");
                    log.info("[语音]"+txt);
                    bean.setTxt( "<audio  src='"+ ossModel.getDomain() + txt +"' />");
                } else if (txt.contains("/" + FTPUtil.FTP_FOLDER.chat_img.name()) || txt.toLowerCase().endsWith(".png")|| txt.toLowerCase().endsWith(".jpg")|| txt.toLowerCase().endsWith(".gif")|| txt.toLowerCase().endsWith(".jpeg")) {
                    //
                    bean.setTxt("<img  style='max-width: 150px;max-height:150px;' class='face' src='" + ossModel.getDomain() + txt + "'>");
                    bean.setPsr("uparse");
                    bean.setSimple_content("[图片]");
                } else {
                    //

                    if (wc.getChatUrlTxtCanLink() == 1) {
                        String t = bean.getTxt().trim();
                        String urlpatternString = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
                        Pattern pattern = Pattern.compile(urlpatternString);
                        Matcher matcher = pattern.matcher(t);
                        String[] ts = new String[]{};
                        while (matcher.find()) {
                            String str = matcher.group();
                            if (!ArrayUtils.contains(ts, str)) {
                                ts = ArrayUtils.add(ts, str);
                            }
                        }

                        txt = bean.getTxt();
                        if (!ArrayUtils.isEmpty(ts)) {
                            for (String o : ts) {
                                if (StringUtils.isEmpty(o) || o.contains("/emotion/"))
                                    continue;
                                txt = txt.replaceAll(o, "<a style='    color: #3F92F8;' href='" + ossModel.getDomain() + o + "'>" + o + "</a>");
                            }
                            bean.setTxt(txt);
                            bean.setPsr("uparse");
                        }
                    }

                    String[] arrs = GroupMethod.group("\\[f(.*?)#(.*?)\\]", txt, 2);
                    if (!ArrayUtils.isEmpty(arrs)) {
                        for (String o : arrs) {
                            if (StringUtils.isEmpty(o)) continue;
                            String[] ss = o.split("!@#&");
                            int i = Integer.valueOf(ss[0]);
                            String f = ".gif";
                            if (i == 1) f = ".png";
                            String img = ossModel.getDomain() + "/" + FTPUtil.FTP_FOLDER.img_sys.name() + "/emotion/face" + (i < 10 ? "0" + i : i) + "/" + ss[1] + f;
//                            log.info("img:" + img);
                            //
                            //txt = txt.replace("[f"+i+"#"+ss[1]+"]", "<img class='face' src='"+img+"'>");
                            if (i == 0) {
                                txt = txt.replace("[f" + i + "#" + ss[1] + "]", "<img  style='max-width: 24px;max-height:24px;' class='face face1' src='" + img + "'>");
                            } else if (i == 1) {
                                txt = txt.replace("[f" + i + "#" + ss[1] + "]", "<img  style='max-width: 100px;max-height:100px;' class='face face2' src='" + img + "'>");
                            } else {
                                txt = txt.replace("[f" + i + "#" + ss[1] + "]", "<img  style='max-width: 150px;max-height:150px;' class='face face3' src='" + img + "'>");
                            }

                        }
                        bean.setTxt(txt);
                        bean.setSimple_content("[表情]");
                    } else {
                        String temp_str = bean.getTxt().replaceAll("<br/>", " ");
                        if (temp_str.length() > 10) {
                            bean.setSimple_content(temp_str.substring(0, 10));
                        } else {
                            bean.setSimple_content(temp_str);
                        }
                    }
                }
                if (message.getCMD().equals(Message.CMD_ENUM.USER_CHAT_SEND_TXT.name())) {
//                    log.info("message:{}",JSONObject.toJSONString(message));
                    log.info("sendRemoteSelf:{}",Message.CMD_ENUM.USER_CHAT_SEND_TXT.name());
                    userChatCmd.sendTXT(bean);
                    //如果对方离线，则通知对方
                    //如果USER_APPPUSH存在，证明正在使用APP端
                    if (ChatStore.USER_APPPUSH.containsKey(bean.getToUid())) {
                        PushMessage pm = new PushMessage();
                        pm.setTitle(bean.getFromName());
                        String s = HtmlUtils.delHTMLTag(bean.getTxt());
                        if (s.length() > 20) {
                            s = s.substring(0, 20);
                        }
                        pm.setContent(s);
                        pm.setUserRole("");
                    }
                } else if (message.getCMD().equals(Message.CMD_ENUM.GROUP_CHAT_SEND_TXT.name())) {
                    log.info("GroupChatSendTxt");
                    groupChatCmd.sendTXT(bean);
                    //4028d817755a9b5f01755aa3386d0003#4028d8177652ad9e017652c948c60000#
                    if (!StringUtils.isEmpty(bean.getAite())) {
                        String[] arrs = bean.getAite().split("#");
                        for (String uid : arrs) {
                            if (StringUtils.isEmpty(uid)) continue;

                            if ("all".equals(uid)) {
                                groupChatCmd.sendAite(bean);
                            } else {
                                groupChatCmd.sendAite(bean, uid);
                            }
                        }
                    }
                }
            } else if (message.getCMD().equals(Message.CMD_ENUM.USER_CHAT_SEND_VOICE.name())
                    || message.getCMD().equals(Message.CMD_ENUM.GROUP_CHAT_SEND_VOICE.name())) {
                ChatTxtBean bean = (ChatTxtBean) JsonUtil.getDTO(message.getBody().toString(), ChatTxtBean.class);
                bean.setOldTxt(bean.getTxt());
                bean.setSimple_content("[语音]");
                //语音
                bean.setPsr("voice");
                bean.setTxt(PropertiesConfig.getResourceHttpPrefix() + bean.getTxt());
                if (message.getCMD().equals(Message.CMD_ENUM.USER_CHAT_SEND_VOICE.name())) {
                    log.info("sendRemoteSelf:{}",Message.CMD_ENUM.USER_CHAT_SEND_VOICE.name());
                    userChatCmd.sendTXT(bean);
                    if (ChatStore.USER_APPPUSH.containsKey(bean.getToUid())) {
                        AppPush ap = ChatStore.USER_APPPUSH.get(bean.getToUid());

                        PushMessage pm = new PushMessage();
                        pm.setTitle(bean.getFromName());
                        pm.setContent("[语音]");
                        pm.setUserRole("");
                    }
                } else if (message.getCMD().equals(Message.CMD_ENUM.GROUP_CHAT_SEND_VOICE.name())) {
                    log.info("GroupChatSendVoice");
                    groupChatCmd.sendTXT(bean);
                }
            } else if (message.getCMD().equals(Message.CMD_ENUM.CHAT_MSG_UNDO.name())) {
                ChatTxtBean bean = (ChatTxtBean) JsonUtil.getDTO(message.getBody().toString(), ChatTxtBean.class);
                MemberBean from = storeComponent.getMemberBeanFromMapDB(bean.getFromUid());
                bean.setFromName(from.getNickName());
                bean.setSimple_content("");
                if (!StringUtils.isEmpty(bean.getToGroupid())) {
                    groupChatCmd.msgUndo(bean);
                } else {
                    userChatCmd.msgUndo(bean);
                }
            } else if (message.getCMD().equals(Message.CMD_ENUM.CHAT_MSG_UNDO_MGR.name())) {
                ChatTxtBean bean = (ChatTxtBean) JsonUtil.getDTO(message.getBody().toString(), ChatTxtBean.class);
                RoomBean rb =chatStoreComponent.getRoomBeanMap(bean.getToGroupid());
                if (!rb.getOwner_UUID().equals(bean.getFromUid())
                        && rb.getMemberMgr_ids().indexOf(bean.getFromUid()) < 0) {
                    return;
                }
                //bean.setFromName(from.getNickName()+"【管理】");
                bean.setFromName("【群管理】");
                bean.setSimple_content("");
                groupChatCmd.msgUndo(bean);
            }else if (message.getCMD().equals(Message.CMD_ENUM.APP_PUSH_USER_INFO.name())) {
                Map map = JsonUtil.getMapFromJson(message.getBody().toString());
                AppPush ap = new AppPush();
                ap.setAppid(map.get("appid").toString());
                ap.setAppkey(map.get("appkey").toString());
                ap.setClientid(map.get("clientid").toString());
                ap.setUserName(map.get("userName").toString());
                ap.setUserRole(map.get("userRole").toString());
                ap.setUid(map.get("uid").toString());
                if (!StringUtils.isEmpty(ap.getClientid())) {
                    ChatStore.USER_APPPUSH.put(map.get("uid").toString(), ap);
                }
            }  else if (message.getCMD().equals(Message.CMD_ENUM.CLEARCHATMSG_SINGLE_CLOUD.name())) {
                //清除云端数据储存
                String key = message.getBody().toString();
                redisService.delete(key);
                String[] arrs = key.split("#");
                String id1 = arrs[0];
                String id2 = arrs[1];
                RoomBean rb =chatStoreComponent.getRoomBeanMap(id2);
                if (!ObjectUtils.isEmpty(rb)) {
                    clearCloudDateAndCount(id1, id2, true);
                } else {
                    clearCloudDateAndCount(id1, id2, false);
                }
            } else if (message.getCMD().equals(Message.CMD_ENUM.CLEARCHATMSG.name())) {
                //CLEARCHATMSG 只存在于用户之间 双向清空记录
                String s = message.getBody().toString();
                String[] arrs = s.split("#");

                //清除云端数据
                redisService.delete(arrs[0] + "#" + arrs[1]);
                redisService.delete(arrs[1] + "#" + arrs[0]);
                clearCloudDateAndCount(arrs[0], arrs[1], false);
                clearCloudDateAndCount(arrs[1], arrs[0], false);
                if (!ObjectUtils.isEmpty(chatStoreComponent.getMemberBean(arrs[1]))) {
                    Message msg = new Message();
                    msg.setBody(arrs[0]);
                    msg.setCMD(Message.CMD_ENUM.CLEARCHATMSG.name());
                    sendUtils.send(arrs[1], msg);
                } else {
                    Long l = chatService.wsmCount(new String[]{"fromUid", "toUid", "cmd"}, new Object[]{arrs[0]
                            , arrs[1], Message.CMD_ENUM.CLEARCHATMSG.name()});
                    if (null == l || l <= 0) {
                        WaitSendMessage wsm = new WaitSendMessage();
                        wsm.setFromUid(arrs[0]);
                        wsm.setCmd(Message.CMD_ENUM.CLEARCHATMSG.name());
                        wsm.setToUid(arrs[1]);
                        chatService.saveWSM(wsm);
                    }
                }
            } else if (message.getCMD().equals(Message.CMD_ENUM.CHAT_MSG_READED.name())) {
                Map map = JsonUtil.getMapFromJson(message.getBody().toString());
                if (storeComponent.isMemberOnline(map.get("toUid").toString())) {
                    Message msg = new Message();
                    msg.setBody(map.get("fromUid").toString());
                    msg.setCMD(Message.CMD_ENUM.CHAT_MSG_READED.name());
                    sendUtils.send(map.get("toUid").toString(), msg);
                } else {
                    Long l = chatService.wsmCount(new String[]{"fromUid", "toUid", "cmd"}, new Object[]{map.get("fromUid").toString()
                            , map.get("toUid").toString(), Message.CMD_ENUM.CHAT_MSG_READED.name()});
                    if (null == l || l <= 0) {
                        WaitSendMessage wsm = new WaitSendMessage();
                        wsm.setFromUid(map.get("fromUid").toString());
                        wsm.setCmd(Message.CMD_ENUM.CHAT_MSG_READED.name());
                        wsm.setToUid(map.get("toUid").toString());
                        chatService.saveWSM(wsm);
                    }
                }
            } else if (message.getCMD().equals(Message.CMD_ENUM.SHOW_INPUT_ING.name())) {
                Map map = JsonUtil.getMapFromJson(message.getBody().toString());
                Message msg = new Message();
                msg.setBody(map.get("fromUid").toString());
                msg.setCMD(Message.CMD_ENUM.SHOW_INPUT_ING.name());
                sendUtils.send(map.get("toUid").toString(), msg);
            } else if (message.getCMD().equals(Message.CMD_ENUM.HIDE_INPUT_ING.name())) {
                Map map = JsonUtil.getMapFromJson(message.getBody().toString());
                Message msg = new Message();
                msg.setBody(map.get("fromUid").toString());
                msg.setCMD(Message.CMD_ENUM.HIDE_INPUT_ING.name());
                sendUtils.send(map.get("toUid").toString(), msg);
            }
        } catch (Exception e) {
            try {
                log.error("", e);
                if (StringUtils.isEmpty(e.getMessage().trim())) return;
                Message message = new Message();
                message.setCMD(Message.CMD_ENUM.ERROR.name());
                message.setBody(e.getMessage());
                synchronized (arg0.toString().intern()) {
                    arg0.sendMessage(new TextMessage(JsonUtil.getJSONString(message)));
                }
            } catch (Exception e1) {
                log.error("",e1);
            }finally {
                currentSessionId.remove(Thread.currentThread());
            }
        }
    }


    private void clearCloudDateAndCount(String uid, String chatid, boolean isRoom) {
        String prefix = uid + "#" + chatid;
        String str = redisService.get(prefix + "_dateStr");
        String[] arrs = str.split(",");
        for (String o : arrs) {
            if (StringUtils.isEmpty(o)) continue;
            redisService.delete(prefix + "_" + o + "_count");
        }
        redisService.delete(prefix + "_dateStr");
    }


    private void reSend(String key, WebSocketSession arg0) throws Exception {

        List<String> CHECKSEND_WAIT_SEND_list = redisService.lRange(key + "#CHECKSEND_WAIT_SEND", 0, 200);
        if (null != CHECKSEND_WAIT_SEND_list && !CHECKSEND_WAIT_SEND_list.isEmpty()) {
            for (String s : CHECKSEND_WAIT_SEND_list) {
                if (StringUtils.isEmpty(s)) continue;
                synchronized (arg0) {
                    arg0.sendMessage(new TextMessage(s));

                    Thread.currentThread().sleep(50);
                }

            }

            redisService.delete(key + "#CHECKSEND_WAIT_SEND");

        }
        //是否有未发送给我的信息。如果有。则推送到前端
        try {
            this.reSend(key.split("#")[0]);
        }catch (Exception e){
            log.error("",e);
        }

    }

    public void reSend(String toUid) {
        List<WaitSendMessage> list = null;
        try {
            list = chatService.getWSMList(new String[]{"toUid"}, new Object[]{toUid});
            if (null != list && !list.isEmpty()) {
                try {
                    for (WaitSendMessage message : list) {
                        try {
                            chatService.deleteWSM(message);
                        } catch (Exception e) {
                            log.error("未读消息删除失败, message:{}",toUid,e);
                        }
                    }
                    reSendWSMDo(list, toUid);
                } catch (Exception e) {
                    log.error("reSend:{}", JSONObject.toJSONString(list), e);
                }
            }
        } catch (Exception e) {
            log.error("reSend:{}", toUid, e);
        }

    }


    private void reSendWSMDo(List<WaitSendMessage> wsmlist, String toUid) {
        List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(toUid);
        for (WebSocketSession TO_WSSESSION : ws_list) {
            if (null != TO_WSSESSION) {

                if (ChatStore.WAIT_SEND_MESSAGE.containsKey(toUid)) {
                    List<Message> list1 = ChatStore.WAIT_SEND_MESSAGE.get(toUid);
                    List<Message> list2 = Lists.newArrayList(list1);
                    for (Message o : list2) {
                        list1.remove(o);
                        sendUtils.send(TO_WSSESSION, o);
                        try {
                            Thread.currentThread().sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                for (WaitSendMessage o : wsmlist) {
                    try {
                        Thread.currentThread().sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        if (!StringUtils.isEmpty(o.getCmd())) {
                            if (o.getCmd().equals(Message.CMD_ENUM.CLEARCHATMSG.name())) {
                                Message msg = new Message();
                                msg.setBody(o.getFromUid());
                                msg.setCMD(Message.CMD_ENUM.CLEARCHATMSG.name());
                                sendUtils.send(o.getToUid(), msg);
                                continue;
                            } else if (o.getCmd().equals(Message.CMD_ENUM.AITE.name())) {
                                Message msg = new Message();
                                msg.setBody(o.getContent());
                                msg.setCMD(Message.CMD_ENUM.AITE.name());
                                sendUtils.send(o.getToUid(), msg);
                                continue;
                            } else if (o.getCmd().equals(Message.CMD_ENUM.CHAT_MSG_READED.name())) {


                                Message msg = new Message();
                                msg.setBody(o.getFromUid());
                                msg.setCMD(Message.CMD_ENUM.CHAT_MSG_READED.name());
                                sendUtils.send(o.getToUid(), msg);
                                continue;
                            }
                        }

                        Message message = new Message();
                        MessageBean msg = new MessageBean();
                        if (!StringUtils.isEmpty(o.getType()) && o.getType().equals(MessageBean.MessageType.USER_CARD.name())) {
                            ChatCardBean bean = new ChatCardBean();
                            bean.setDate(o.getDate());
                            bean.setUuid(o.getUuid());
                            bean.setFromHeadpic(o.getHeadpic());
                            bean.setFromUid(o.getFromUid());
                            bean.setFromName(o.getName());
                            bean.setToUid(o.getToUid());
                            bean.setSimple_content(o.getSimple_content());
                            if (!StringUtils.isEmpty(bean.getToGroupid())) {
                                msg.setChatid(bean.getToGroupid());
                                bean.setChatid(bean.getToGroupid());
                                msg.setChatType("1");
                            } else {
                                bean.setChatid(o.getChatid());
                                msg.setChatid(o.getChatid());
                                msg.setChatType("2");
                            }
                            bean.setMheadpic(o.getMheadpic());
                            bean.setMid(o.getMid());
                            bean.setMname(o.getMname());
                            bean.setMuuid(o.getMuuid());
                            msg.setBean(bean);
                        } else {
                            ChatTxtBean txtBean = new ChatTxtBean();
                            txtBean.setDate(o.getDate());
                            txtBean.setTxt(o.getContent());
                            txtBean.setOldTxt(o.getOldContent());
                            txtBean.setUuid(o.getUuid());
                            txtBean.setSimple_content(o.getSimple_content());
                            txtBean.setFromHeadpic(o.getHeadpic());
                            txtBean.setFromUid(o.getFromUid());
                            txtBean.setFromName(o.getName());
                            txtBean.setToUid(o.getToUid());
                            txtBean.setToGroupid(o.getToGroupid());
                            txtBean.setPsr(o.getPsr());
                            txtBean.setSub_txt(o.getSub_txt());
                            if (!StringUtils.isEmpty(txtBean.getToGroupid())) {
                                //
                                msg.setChatid(txtBean.getToGroupid());
                                txtBean.setChatid(txtBean.getToGroupid());
                                msg.setChatType("1");
                            } else {
                                //
                                msg.setChatid(o.getChatid());
                                msg.setChatType("2");
                            }
                            msg.setBean(txtBean);
                        }
                        msg.setType(o.getType());
                        if (!StringUtils.isEmpty(o.getCmd())) {
                            message.setBody(msg);
                            message.setCMD(o.getCmd());
                        } else {
                            message.setBody(Lists.newArrayList(msg));
                            if ("1".equals(msg.getChatType())) {
                                //群
                                message.setCMD(Message.CMD_ENUM.GROUP_CHAT_MESSAGE.name());
                            } else {
                                //用户
                                message.setCMD(Message.CMD_ENUM.USER_CHAT_MESSAGE.name());
                            }
                        }
                        sendUtils.send(TO_WSSESSION, message);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }
        }

    }

    private void chongxian_check(String key, String app_uuid) {
//        try {
//            if (SessionStore.USERID_WS_MAP_containsKey(key)
//                    && SessionStore.USERID_APPUUID_MAP.containsKey(key) &&
//                    !SessionStore.USERID_APPUUID_MAP.get(key).equals(app_uuid)
//            ) {
//                //冲线
//
//
//                Message msg = new Message();
//                msg.setBody("");
//                msg.setCMD(Message.CMD_ENUM.OTHER_LOGIN.name());
//                sendUtils.sendByKey(key, msg);
//                Thread.currentThread().sleep(500);
//                WebSocketSession temp = SessionStore.USERID_WS_MAP_GET_ByKey(key);
//                SessionStore.WS_USERID_MAP.remove(temp);
//                SessionStore.USERID_WS_MAP_REMOVE(key);
//                /**将缓存在redis的请求地址删除**/
//                redisService.hDelete(SessionStore.REDIS_WSS_KEY,key.indexOf("#")>0?key.substring(0, key.indexOf("#")):key);
//                redisService.hDelete(SessionStore.ONLINE_MEMBER,key);
//                Thread.currentThread().sleep(50);
//            }
//        } catch (InterruptedException e) {
//            log.error("", e);
//        }
    }


    @Override
    public void handleTransportError(WebSocketSession arg0, Throwable arg1)
            throws Exception {
//		//
    }

    /**
     * 统一设置putsession时需要缓存的信息
     * @param map 前端携带的信息
     */
    private void putSessionInitCache(Map map,WebSocketSession session) throws Exception{
        /**userId memberId#设备信息*/
        String userId=map.get("user_id").toString();
        String addressIp = RabbitmqConfig.getLocalIP();
        String port = environment.getProperty("server.port");

        /***存放到redis**/
        /***存入redis不需要带设备号，只标识在哪条服务器**/
        String uId = userId.split("#")[0];
        redisService.hPut(SessionStore.REDIS_WSS_KEY, uId, addressIp + port);
        /***存入redis需要带设备号，提供给后端按条件查询**/
        redisService.hPut(SessionStore.ONLINE_MEMBER, userId, uId);
        /***存入redis 特权用户，多人同时登录一个账号，需要sessionID作为唯一标识，**/
        redisService.hPut(uId, session.getId(), addressIp + port);
        /***本地缓存 特权用户，多人同时登录一个账号，需要sessionID作为唯一标识，**/
        SessionStore.putUserWebSocketList(userId, session.getId(), session);
        /**存入redis，放入设备信息*/
        if (redisService.hasKey(userId.split("#")[0] + "_LOGIN_TERMINAL")) {
            String str = redisService.get(userId.split("#")[0] + "_LOGIN_TERMINAL");
            if (str.indexOf(userId.split("#")[1]) < 0) {
                str += ("#" + userId.split("#")[1]);
                redisService.set(userId.split("#")[0] + "_LOGIN_TERMINAL", str);
            }
        } else {
            redisService.set(userId.split("#")[0] + "_LOGIN_TERMINAL",userId.split("#")[1]);
        }
        SessionStore.USERID_APPUUID_MAP.put(userId, map.get("app_uuid").toString());
        /***本地缓存  以session为key，当发送信息时可以取出memberId#设备**/
        SessionStore.WS_USERID_MAP.put(session, userId);
        SessionStore.SESSIONID_MEMBERID_LIST_MAP.put(session.getId(),userId);
        List<String > closeWsIds=SessionStore.removerWebSocketIsClose(userId);
        closeWsIds.forEach(wsId->{
            redisService.hDelete(userId,wsId);
        });
    }
}
