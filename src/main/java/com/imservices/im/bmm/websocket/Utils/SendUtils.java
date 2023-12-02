package com.imservices.im.bmm.websocket.Utils;

import com.alibaba.fastjson.JSON;
import com.imservices.im.bmm.bean.ChatCardBean;
import com.imservices.im.bmm.bean.ChatTxtBean;
import com.imservices.im.bmm.bean.MessageBean;
import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.chat.SendThread;
import com.imservices.im.bmm.entity.WaitSendMessage;
import com.imservices.im.bmm.mq.RabbitmqConfig;
import com.imservices.im.bmm.remote.MessageRemoteService;
import com.imservices.im.bmm.remote.RemoteMessage;
import com.imservices.im.bmm.service.ChatService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.WebSocketSessionDto;
import com.imservices.im.bmm.websocket.core.ChatCoreWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Component
@Slf4j
@Lazy
public class SendUtils {

    @Resource
    private RedisService redisService;

    @Resource
    private Environment environment;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private ChatService cService;

    @Resource
    private SendThread sendThread;

    @Resource
    private MessageRemoteService messageRemoteService;




    //向某个用户发送信息
    public void send(WebSocketSession session, Message message)  {
        //使用线程有效防止群组达到一定人数后 接收延迟问题
        try {
            sendThread.run(session,message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //向群发送信息
    public void send(RoomBean room, Message message, ChatService chatService) {
        try {
            synchronized (room.getId().intern()) {
                String[] uids = room.getMember_ids().split("#");
                HashSet<String> uidHash = new HashSet<>(Arrays.asList(uids));
                for (String uid : uidHash) {
                    if (StringUtils.isEmpty(uid)) continue;
                    List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(uid);
                    boolean needSendWaitMessage = true;
                    for (WebSocketSession ws : ws_list) {
                        if (this.isCurrentSession(ws)){
                            needSendWaitMessage = false;
                            continue;
                        }

                        CompletableFuture.runAsync(()->{
                            synchronized (ws.getId().intern()) {
                                try {
                                    send(ws, message);
                                } catch (Exception e) {
                                    log.error("",e);
                                    sendRemoteWaitMessage(uid,message);
                                }
                            }
                        });
                        needSendWaitMessage = false;
                    }

//                    log.info("sendRemoteGroupMessage:{}",uids);
                    //如果不存在与本地，则到redis里获取配置
                    boolean sendRemoteGroupMessageSuccess = this.sendRemoteGroupMessage(uid, message);
                    if (needSendWaitMessage){
                        needSendWaitMessage = sendRemoteGroupMessageSuccess;
                    }

                    /**
                     * 使用队列来发送不在线
                     */
                    if (needSendWaitMessage) {
                        sendRemoteWaitMessage(uid,message);
                    }
                }
            }
        } catch (Exception e) {
            log.error("",e);
        }
    }


    public void send(RoomBean room, Message message) {
        ChatService chatService = null;
        send(room, message, chatService);
    }

    public void send(String uid, Message message, ChatService chatService) {
        try {

            List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(uid);

            boolean needSendWaitMessage = true;
//            log.info("推送的多少端---1:{},message:{}",ws_list.size(),message.getBody());
            int ii =0;
            for (WebSocketSession ws : ws_list) {
                if (this.isCurrentSession(ws)) {
                    needSendWaitMessage = false;
                    continue;
                }
                ii++;
                if (null != ws) {
                    CompletableFuture.runAsync(() -> {
                        synchronized (ws.getId().intern()) {
                            try {
                                send(ws, message);
                            } catch (Exception e) {
                                log.error("", e);
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    needSendWaitMessage = false;
                }
            }
//            log.info("推送的多少端---2:{},message:{}",ii,message.getBody());

            log.info("sendRemoteSelf:{}",uid);
            boolean remoteSelfSendSuccess= this.sendRemoteSelf(uid,message);
            if (needSendWaitMessage) {
                needSendWaitMessage = !remoteSelfSendSuccess;
            }
            if (needSendWaitMessage) {

                if (null != chatService) {

                    if (message.getCMD().toString().equals(Message.CMD_ENUM.AITE.name())) {

                        WaitSendMessage wsm = new WaitSendMessage();
                        wsm.setCmd(Message.CMD_ENUM.AITE.name());
                        wsm.setContent(message.getBody().toString());
                        wsm.setToUid(uid);
                        chatService.saveWSM(wsm);

                    } else {

                        List<MessageBean> list = (List<MessageBean>) message.getBody();
                        MessageBean mb = list.get(0);

                        ChatTxtBean bean = (ChatTxtBean) mb.getBean();
                        //如果对方不在线，则把信息保存到数据库，等他上线后发送
                        WaitSendMessage wsm = new WaitSendMessage();
                        wsm.setSimple_content(bean.getSimple_content());
                        wsm.setUuid(bean.getUuid());
                        wsm.setChatid(mb.getChatid());
                        wsm.setContent(bean.getTxt());
                        wsm.setOldContent(bean.getOldTxt());
                        wsm.setCreateDate(new Date());
                        wsm.setDate(bean.getDate());
                        wsm.setToGroupid(bean.getToGroupid());
                        wsm.setFromUid(bean.getFromUid());
                        wsm.setHeadpic(bean.getFromHeadpic());
                        wsm.setName(bean.getFromName());
                        wsm.setToUid(uid);
                        wsm.setType(mb.getType());
                        wsm.setPsr(bean.getPsr());
                        wsm.setSub_txt(bean.getSub_txt());
                        chatService.saveWSM(wsm);
                    }
                }
            }
        } catch (Exception e) {
            log.error("",e);
        }
    }


    public void sendByMQ(String uid, Message message, ChatService chatService) {
        try {

            List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(uid);

            boolean flag = true;
            for (WebSocketSession ws : ws_list) {
                if (this.isCurrentSession(ws)){
                    flag = false;
                    continue;
                }
                if (null != ws) {
                    CompletableFuture.runAsync(()->{
                        synchronized (ws.getId().intern()) {
                            try {
                                send(ws, message);
                            } catch (Exception e) {
                                log.error("",e);
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    flag = false;
                }
            }

            if (flag) {

                if (null != chatService) {

                    if (message.getCMD().toString().equals(Message.CMD_ENUM.AITE.name())) {

                        WaitSendMessage wsm = new WaitSendMessage();
                        wsm.setCmd(Message.CMD_ENUM.AITE.name());
                        wsm.setContent(message.getBody().toString());
                        wsm.setToUid(uid);
                        chatService.saveWSM(wsm);

                    } else {

                        List<MessageBean> list = (List<MessageBean>) message.getBody();
                        MessageBean mb = list.get(0);

                        ChatTxtBean bean = (ChatTxtBean) mb.getBean();
                        //如果对方不在线，则把信息保存到数据库，等他上线后发送
                        WaitSendMessage wsm = new WaitSendMessage();
                        wsm.setSimple_content(bean.getSimple_content());
                        wsm.setUuid(bean.getUuid());
                        wsm.setChatid(mb.getChatid());
                        wsm.setContent(bean.getTxt());
                        wsm.setOldContent(bean.getOldTxt());
                        wsm.setCreateDate(new Date());
                        wsm.setDate(bean.getDate());
                        wsm.setToGroupid(bean.getToGroupid());
                        wsm.setFromUid(bean.getFromUid());
                        wsm.setHeadpic(bean.getFromHeadpic());
                        wsm.setName(bean.getFromName());
                        wsm.setToUid(uid);
                        wsm.setType(mb.getType());
                        wsm.setPsr(bean.getPsr());
                        wsm.setSub_txt(bean.getSub_txt());
                        chatService.saveWSM(wsm);
                    }
                }
            }
        } catch (Exception e) {
            log.error("",e);
        }
    }



    public void send(String uid, Message message) {
//        boolean flag = true;
        try {
            List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(uid);
            for (WebSocketSession ws : ws_list) {
//                flag = false;
                if (null != ws) {
                    CompletableFuture.runAsync(()->{
                        synchronized (ws.getId().intern()) {
                            try {
                                send(ws, message);
                            } catch (Exception e) {
                                log.error("",e);
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }

            this.sendRemoteGroupMessage(uid, message);

        } catch (Exception e) {
            log.error("",e);
        }
    }


    public void send(RoomBean room, Message message, String[] excludeUid) {
        try {
            synchronized (room.getId().intern()) {
                String[] uids = room.getMember_ids().split("#");
                for (String uid : uids) {
                    if (StringUtils.isEmpty(uid)) continue;
                    if (!ArrayUtils.isEmpty(excludeUid)) {
                        boolean flag = false;
                        for (String o : excludeUid) {
                            if (uid.equals(o)) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) continue;
                    }
                    List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(uid);
                    for (WebSocketSession ws : ws_list) {
                       if (this.isCurrentSession(ws)){
                           continue;
                       }
                        if (null != ws) {
                            synchronized (ws.getId().intern()) {
                                send(ws, message);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("",e);
        }
    }

    public void sendSyncChatMsg(WebSocketSession session, String messageJsonStr) {
        try {
            if (null == session) return;
            synchronized (session.getId().intern()) {


                session.sendMessage(new TextMessage(messageJsonStr));
                Thread.sleep(50);
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("",e);
        }

    }


    /**
     * 对发送对象为其它服务器时将信息发送到队列进行处理
     * return 返回false表示不存在当前服务器，已经发送到队列远程执行
     */
    public boolean sendRemoteGroupMessage(String userId, Message message) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("userId", userId);
//        map.put("message", message);
//        //从redis缓存中取出来，如果存在，则判断所在服务器
//        try{
//            Object ipObj = redisService.hGet(SessionStore.REDIS_WSS_KEY, (userId));
//            if (ipObj != null) {
//                if (!ipObj.toString().equals(RabbitmqConfig.getLocalIP() + serverPort)) {
//                    log.info("sendRemoteGroupMessage");
//                    rabbitTemplate.convertAndSend(RabbitmqConfig.BASIC_EXCHANGE,
//                            RabbitmqConfig.GROUP_ROUTING_KEY + "." + ipObj.toString(), JSON.toJSONString(map));
//                    return Boolean.FALSE;
//                }
//            }
//        }catch (Exception e){
//            //放进队列，稍后重试
//            rabbitTemplate.convertAndSend(RabbitmqConfig.BASIC_EXCHANGE,
//                    RabbitmqConfig.GROUP_EXCEPTION_ROUTING_KEY, JSON.toJSONString(map));
//            log.error("sendRemoteGroupMessage",e);
//            return Boolean.FALSE;
//        }
//        return Boolean.TRUE;

        return  messageRemoteService.requestRemoteGroupMessage("im-cluster", RemoteMessage.builder()
                .message(message)
                .toUid(userId)
                .CMD(message.getCMD())
                .build());
    }

    /**
     * 处理从队列中获取到的消息
     *
     * @param uid
     * @param message
     */
    public void processRemoteGroupMessage(String uid, Message message) {
        List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(uid);
        try {
            for (WebSocketSession ws : ws_list) {
                if (null != ws) {
                    synchronized (ws.getId().intern()) {
                        send(ws, message);
                    }
                }
            }
        } catch (Exception e) {
            log.error("",e);
        }
    }

//    public void processRemoteGroupMessage(String uid, Message message) {
//        messageRemoteService.requestRemoteGroupMessage("im-cluster", RemoteMessage.builder()
//                .message(message)
//                .toUid(uid)
//                .build());
//    }


    /**
     * 发送等待消息到队列
     * @param userId
     * @param message
     * @return
     */
    public void sendRemoteWaitMessage(String userId, Message message) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("message", message);
        rabbitTemplate.convertAndSend(RabbitmqConfig.BASIC_EXCHANGE,
                RabbitmqConfig.WAIT_MSG_ROUTING_KEY, JSON.toJSONString(map));
    }

    /**
     * 处理从队列中获取到的消息
     *
     * @param uid
     * @param message
     */
    public void saveWaitMsg(Message message,String uid)throws Exception {
        List<MessageBean> list = JSON.parseArray(JSON.toJSONString(message.getBody()),MessageBean.class);
        MessageBean mb = list.get(0);

        if (mb.getType().equals(MessageBean.MessageType.USER_CARD.name())) {
            ChatCardBean bean = JSON.parseObject(JSON.toJSONString(mb.getBean()),ChatCardBean.class);
            WaitSendMessage wsm = new WaitSendMessage();
            wsm.setSimple_content(bean.getSimple_content());
            wsm.setUuid(bean.getUuid());
            wsm.setChatid(mb.getChatid());
            wsm.setCreateDate(new Date());
            wsm.setDate(bean.getDate());
            wsm.setToGroupid(bean.getToGroupid());
            wsm.setFromUid(bean.getFromUid());
            wsm.setHeadpic(bean.getFromHeadpic());
            wsm.setName(bean.getFromName());
            wsm.setToUid(uid);
            wsm.setType(mb.getType());
            wsm.setMheadpic(bean.getMheadpic());
            wsm.setMid(bean.getMid());
            wsm.setMname(bean.getMname());
            wsm.setMuuid(bean.getMuuid());
            cService.saveWSM(wsm);

        } else {
            ChatTxtBean bean = JSON.parseObject(JSON.toJSONString(mb.getBean()),ChatTxtBean.class);
            //如果对方不在线，则把信息保存到数据库，等他上线后发送
            WaitSendMessage wsm = new WaitSendMessage();
            wsm.setSimple_content(bean.getSimple_content());
            wsm.setUuid(bean.getUuid());
            wsm.setChatid(mb.getChatid());
            wsm.setContent(bean.getTxt());
            wsm.setOldContent(bean.getOldTxt());
            wsm.setCreateDate(new Date());
            wsm.setDate(bean.getDate());
            wsm.setToGroupid(bean.getToGroupid());
            wsm.setFromUid(bean.getFromUid());
            wsm.setHeadpic(bean.getFromHeadpic());
            wsm.setName(bean.getFromName());
            wsm.setToUid(uid);
            wsm.setType(mb.getType());
            wsm.setPsr(bean.getPsr());
            wsm.setSub_txt(bean.getSub_txt());
            cService.saveWSM(wsm);
        }
    }

    /**
     * 对发送对象为其它服务器时将信息发送到队列进行处理
     */
    public boolean processRemoteUser(ChatTxtBean bean) {
        Boolean rs = Boolean.FALSE;
        //获取本机所有的长链接
        List<WebSocketSession> list = SessionStore.USERID_WS_MAP_GET_ByUid(bean.getToUid());
        //获取所有存放在redis的长链接所在服务器ip，在putSessionInitCache 初始化
        Map sessionMap = redisService.hGetAll(bean.getToUid());
        if (list.size() > 0) {
            //本地存在相应的长链接，发送信息
            rs = Boolean.TRUE;
            //先移除存放在本地的长链接对应信息
            if (!sessionMap.isEmpty()) {
                list.forEach(ws -> {
                    sessionMap.remove(ws.getId());
                });
            }
        }
        HashMap ipList=new HashMap();
        sessionMap.forEach((k, ip) -> {
            ipList.put(ip,k);
        });
        ipList.forEach((ip, v) -> {
            if (ip.toString().indexOf(RabbitmqConfig.getLocalIP() + serverPort) < 0) {
                rabbitTemplate.convertAndSend(RabbitmqConfig.BASIC_EXCHANGE, RabbitmqConfig.USER_ROUTING_KEY + "." + ip, JSON.toJSONString(bean));
            }
        });
        return rs;
    }

    /***
     * 发送到对应服务器，处理多端用户
     * @param uid
     * @param message
     */
//    public void sendRemoteSelf(String uid, Message message){
//        HashMap map=new HashMap();
//        map.put("uid",uid);
//        map.put("message",message);
//        Map<Object, Object> sessionMap =redisService.hGetAll(uid);
//        HashMap smap=sessionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue,Map.Entry::getKey,(oldVal, newVal) -> newVal, LinkedHashMap::new));
//        smap.forEach((ip,v)->{
//            if(ip.toString().indexOf(RabbitmqConfig.getLocalIP()+serverPort)<0) {
//                rabbitTemplate.convertAndSend(RabbitmqConfig.BASIC_EXCHANGE,
//                        RabbitmqConfig.SELF_MESSAGE_ROUTING_KEY + "." + ip, JSON.toJSONString(map));
//                log.error("发送到对应服务器，处理多端用户ip:{},uid={},message{}",ip,uid,JSON.toJSONString(message));
//            }
//        });
//    }

    public boolean sendRemoteSelf(String uid, Message message){
     return messageRemoteService.requestRemoteSefMessage("im-cluster", RemoteMessage.builder()
               .toUid(uid)
               .message(message)
               .CMD(message.getCMD())
               .build()
       );
    }

    private boolean isCurrentSession(WebSocketSession ws){
        WebSocketSessionDto webSocketSession = new WebSocketSessionDto(ws);
        String currentSessionId = ChatCoreWebSocketHandler.currentSessionId.get(Thread.currentThread());
        if (StringUtils.isEmpty(currentSessionId)){
            return false;
        }
        if (!StringUtils.isEmpty(webSocketSession.getSessionId())){
            return currentSessionId.equals(webSocketSession.getSessionId());
        }
//        log.info("ws:{},currentSessionId:{}",webSocketSession.getSessionId(),currentSessionId);
        return false;
    }
}
