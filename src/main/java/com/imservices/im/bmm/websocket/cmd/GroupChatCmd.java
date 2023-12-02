package com.imservices.im.bmm.websocket.cmd;

import com.google.common.collect.Lists;
import com.imservices.im.bmm.bean.*;
import com.imservices.im.bmm.bean.store.ChatStoreComponent;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.entity.AccessRecord;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.entity.WaitSendMessage;
import com.imservices.im.bmm.remote.MessageRemoteService;
import com.imservices.im.bmm.remote.RemoteMessage;
import com.imservices.im.bmm.service.*;
import com.imservices.im.bmm.utils.JsonUtil;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class GroupChatCmd {


    protected WebConfigService configService;
    private StoreComponent storeComponent;

    protected MemberService memberService;

    private ChatService chatService;

    private AccessRecordService accessRecordService;

    private AccessRecordCmd accessRecordCmd;

    private SendUtils sendUtils;

    private ChatStoreComponent chatStoreComponent;

    private RoomMemberService roomMemberService;

    private MessageRemoteService messageRemoteService;

    //	@Transactional
    public void sendTXT(ChatTxtBean bean) throws Exception {
        MemberBean fromMember = storeComponent.getMemberBeanFromMapDB(bean.getFromUid());
        if (null == fromMember) throw new Exception("用户不存在");
        RoomBean room = chatStoreComponent.getRoomBeanMap(bean.getToGroupid());
        if (room == null ){
            log.info("群组不存在 room_id:{}",bean.getToGroupid());
            throw new Exception("群组不存在");
        }
        if (!room.getMember_ids().contains(bean.getFromUid())) {
            throw new Exception("你并不是该群成员");
        }

        if (!bean.getFromUid().equals(room.getOwner_UUID())
                && !room.getMemberMgr_ids().contains(bean.getFromUid())) {
            if (!StringUtils.isEmpty(room.getProperties())) {
                Map prosMap = JsonUtil.getMapFromJson(room.getProperties());
                if (Integer.parseInt(prosMap.get("STOPSPEAK").toString()) == 1) {
                    throw new Exception("此群禁止发言");
                }
            }

        }

        if (room.getStopspeak_member_ids().contains(bean.getFromUid())) {
            throw new Exception("禁止发言");
        }


        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
        bean.setDate(sdf.format(new Date()));
        if (StringUtils.isEmpty(fromMember.getHeadpic())) {
            bean.setFromHeadpic("/img_sys/defaultHeadPic.jpg");
        } else {
            bean.setFromHeadpic(fromMember.getHeadpic());
        }


        bean.setDate(sdf.format(new Date()));
        bean.setFromName(fromMember.getNickName());

        MessageBean msg = new MessageBean();
        msg.setChatType("1");
        msg.setChatid(bean.getToGroupid());
        msg.setType(MessageBean.MessageType.USER_TXT.name());
        msg.setBean(bean);

        Message message = new Message();
        message.setBody(Lists.newArrayList(msg));
        message.setCMD(Message.CMD_ENUM.GROUP_CHAT_MESSAGE.name());

        List<String> uids = Arrays.stream(room.getMember_ids().split("#")).distinct().collect(Collectors.toList());

        String sendMemberIds = "";
        for (String uid : uids) {
            if (StringUtils.isEmpty(uid)) {
                continue;
            }
            MemberBean memberBean = chatStoreComponent.getMemberBean(uid);
            if (null == memberBean || "".equals(memberBean.getMember_type()) || memberBean.getMember_type() == Member.MEMBER_TYPE.ROBOT) {
                continue;
            }
            /**不在线的不发*/
            if (!storeComponent.isMemberOnline(uid)) {
                continue;
            }
//            if (uid.equals(bean.getFromUid())){
//                // 自己通过自己的消息通道发
//                continue;
//            }
            sendMemberIds += uid + "#";
            //更新对方记录顺序
            CompletableFuture.runAsync(() -> {
                try {
                    List<AccessRecord> arlist = accessRecordService.getList(new String[]{"uid", "typeid", "entityid"}, new Object[]{uid, "1", bean.getToGroupid()});
//                    AccessRecordBean arbean = null;
                    if (null != arlist && !arlist.isEmpty()) {
                        AccessRecord ar = arlist.get(0);
                        ar.setCreateDate(new Date());
                        accessRecordService.update(ar);
//                        arbean = BeanUtils.accessRecordToBean(ar);
//                        accessRecordCmd.insertOrUpdate(arbean, uid, CMD_ENUM.AR_UPDATE);
                    } else {
                        AccessRecord ar = new AccessRecord();
                        ar.setCreateDate(new Date());
                        ar.setUid(uid);
                        ar.setEntityid(bean.getToGroupid());
                        ar.setTypeid("1");
                        //Member e = memberService.get(bean.getFromUid());
                        if (StringUtils.isEmpty(room.getImg())) {
                            ar.setImg("/img_sys/defaultHeadPic.jpg");
                        } else {
                            ar.setImg(room.getImg());
                        }
                        ar.setTitle(room.getName());
                        accessRecordService.save(ar);
//                        arbean = BeanUtils.accessRecordToBean(ar);
//                        accessRecordCmd.insertOrUpdate(arbean, uid, CMD_ENUM.AR_INSERT);
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            });

        }


        //只对不是机器人的用户发送信息
        room.setMember_ids(sendMemberIds);
        log.info("GroupChatSend");
        sendUtils.send(room, message, chatService);

        //把自己发的信息处理包装后并反馈给自己前端处理
//        MessageBean sendFromUser = new MessageBean();
//        sendFromUser.setChatType("2");
//        sendFromUser.setChatid(bean.getToUid());
//        sendFromUser.setType(MessageBean.MessageType.USER_TXT.name());
//        sendFromUser.setBean(bean);
//        Message messageSelf = new Message();
//        messageSelf.setBody(Lists.newArrayList(sendFromUser));
//        messageSelf.setCMD(CMD_ENUM.GROUP_CHAT_MESSAGE.name());
//        sendUtils.send(bean.getFromUid(), messageSelf, chatService);
    }

    public void msgUndo(ChatTxtBean bean) throws Exception {
        RoomBean room = chatStoreComponent.getRoomBeanMap(bean.getToGroupid());

        if (!room.getMember_ids().contains(bean.getFromUid())) {
            throw new Exception("你并不是该群成员");
        }
        String member_ids = roomMemberService.getRoomMemberIdsByRoomId(new String[]{"room_id"}, new Object[]{bean.getToGroupid()});
        List<String> arrs = Arrays.stream(room.getMember_ids().split("#")).distinct().collect(Collectors.toList());
        String sendMemberIds = "";
        if (!arrs.isEmpty()) {
            for (String uid : arrs) {
                if (StringUtils.isEmpty(uid)) {
                    continue;
                }
                MemberBean memberBean = chatStoreComponent.getMemberBean(uid);
                if (null == memberBean || "".equals(memberBean.getMember_type()) || memberBean.getMember_type() == Member.MEMBER_TYPE.ROBOT) {
                    continue;
                }
                /*不在线的不发*/
                if (!storeComponent.isMemberOnline(uid)) {
                    continue;
                }
                sendMemberIds += uid + "#";
                if (!"-1".equals(bean.getToUid())) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            MessageBean msg = new MessageBean();
                            msg.setChatid(bean.getToGroupid());
                            msg.setBean(bean);
                            Message message = new Message();
                            message.setBody(msg);
                            message.setCMD(Message.CMD_ENUM.CHAT_MSG_UNDO.name());
                            if (sendUtils.sendRemoteGroupMessage(uid, message)) {
                                List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(uid);
                                for (WebSocketSession TO_WSSESSION : ws_list) {
                                    if (null != TO_WSSESSION) {
                                        sendUtils.send(TO_WSSESSION, message);
                                    } else {
                                        //如果对方不在线，则把信息保存到数据库，等他上线后发送
                                        WaitSendMessage wsm = new WaitSendMessage();
                                        wsm.setUuid(bean.getUuid());
                                        wsm.setSimple_content(bean.getSimple_content());
                                        wsm.setCmd(Message.CMD_ENUM.CHAT_MSG_UNDO.name());
                                        wsm.setName(bean.getFromName());
                                        wsm.setChatid(bean.getToGroupid());
                                        wsm.setContent(bean.getTxt());
                                        wsm.setFromUid(bean.getFromUid());
                                        wsm.setToUid(uid);
                                        chatService.saveWSM(wsm);
                                    }
                                }
                            }
                            messageRemoteService.requestRemoteGroupMessage("im-cluster", RemoteMessage.builder()
                                    .message(message)
                                    .toUid(bean.getToUid())
                                    .CMD(message.getCMD())
                                    .build());
                        } catch (Exception e) {
                            log.error("", e);
                        }
                    });
                }
            }
        }
        room.setMember_ids(sendMemberIds);
        MessageBean msg = new MessageBean();
        msg.setChatid(bean.getToGroupid());
        msg.setBean(bean);
        Message message = new Message();
        message.setBody(msg);
        message.setCMD(Message.CMD_ENUM.CHAT_MSG_UNDO.name());
        List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(bean.getFromUid());
        for (WebSocketSession FROM_WSSESSION : ws_list) {
            if (null != FROM_WSSESSION) {
                CompletableFuture.runAsync(() -> {
                    try {
                        sendUtils.send(FROM_WSSESSION, message);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                });
            }
        }
        messageRemoteService.requestRemoteGroupMessage("im-cluster", RemoteMessage.builder()
                .message(message)
                .toUid(bean.getFromUid())
                .CMD(message.getCMD())
                .build());
    }

    public void sendCard(ChatCardBean bean) throws Exception {
//		RoomBean mb = ChatStore.ROOMB_BEAN_MAP.get(bean.getToGroupid());
        RoomBean mb = chatStoreComponent.getRoomBeanMap(bean.getToGroupid());
//		if(null==map||map.isEmpty()) return;//如果此群没有任何用户在，则不能群发信息
        //Member fromMember = memberService.get(bean.getFromUid());
        MemberBean fromMember = storeComponent.getMemberBeanFromMapDB(bean.getFromUid());
        if (null == fromMember) throw new Exception("用户不存在");
//		Room room = roomService.get(bean.getToGroupid());
//		RoomBean room = ChatStore.ROOMB_BEAN_MAP.get(bean.getToGroupid());
        RoomBean room = chatStoreComponent.getRoomBeanMap(bean.getToGroupid());
        if (room.getMember_ids().indexOf(bean.getFromUid()) < 0) {
            throw new Exception("你并不是该群成员");
        }
        if (!bean.getFromUid().equals(room.getOwner_UUID())
                && room.getMemberMgr_ids().indexOf(bean.getFromUid()) < 0) {
            if (!StringUtils.isEmpty(room.getProperties())) {
                Map prosMap = JsonUtil.getMapFromJson(room.getProperties());
                if (Integer.valueOf(prosMap.get("STOPSPEAK").toString()) == 1) {
                    throw new Exception("此群禁止发言");
                }
            }

        }
        //

//		if(null!=room.getEndDate()) {
//			if(room.getEndDate().getTime()<=new Date().getTime()) {
//				throw new Exception("此房间已过期！");
//			}
//		}

        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
        bean.setDate(sdf.format(new Date()));
        if (StringUtils.isEmpty(fromMember.getHeadpic())) {
            bean.setFromHeadpic("/img_sys/defaultHeadPic.jpg");
        } else {
            bean.setFromHeadpic(fromMember.getHeadpic());
        }


        bean.setDate(sdf.format(new Date()));
        bean.setFromName(fromMember.getNickName());

        MessageBean msg = new MessageBean();
        msg.setChatType("1");
        msg.setChatid(bean.getToGroupid());
        msg.setType(MessageBean.MessageType.USER_CARD.name());
        msg.setBean(bean);

        Message message = new Message();
        message.setBody(Lists.newArrayList(msg));
        message.setCMD(Message.CMD_ENUM.GROUP_CHAT_MESSAGE.name());

        List<String> uids = Arrays.stream(room.getMember_ids().split("#")).distinct().collect(Collectors.toList());
        String sendMemberIds = "";
        for (String uid : uids) {
            if (StringUtils.isEmpty(uid)) continue;

            if (StringUtils.isEmpty(uid)) {
                continue;
            }
            MemberBean memberBean = chatStoreComponent.getMemberBean(uid);
            if (null == memberBean || "".equals(memberBean.getMember_type()) || memberBean.getMember_type() == Member.MEMBER_TYPE.ROBOT) {
                continue;
            }
            /**不在线的不发*/
            if (!storeComponent.isMemberOnline(uid)) {
                continue;
            }
            sendMemberIds += uid + "#";


            CompletableFuture.runAsync(() -> {
                try {
                    //更新对方记录顺序
                    List<AccessRecord> arlist = accessRecordService.getList(new String[]{"uid", "typeid", "entityid"}, new Object[]{uid, "1", bean.getToGroupid()});
                    AccessRecordBean arbean = null;
                    if (null != arlist && !arlist.isEmpty()) {
                        AccessRecord ar = arlist.get(0);
                        ar.setCreateDate(new Date());
                        accessRecordService.update(ar);
//                        arbean = BeanUtils.accessRecordToBean(ar);
//                        accessRecordCmd.insertOrUpdate(arbean, uid, CMD_ENUM.AR_UPDATE);
                    } else {
                        AccessRecord ar = new AccessRecord();
                        ar.setCreateDate(new Date());
                        ar.setUid(uid);
                        ar.setEntityid(bean.getToGroupid());
                        ar.setTypeid("1");
                        //Member e = memberService.get(bean.getFromUid());
                        if (StringUtils.isEmpty(room.getImg())) {
                            ar.setImg("/img_sys/defaultHeadPic.jpg");
                        } else {
                            ar.setImg(room.getImg());
                        }
                        ar.setTitle(room.getName());
                        accessRecordService.save(ar);
//                        arbean = BeanUtils.accessRecordToBean(ar);
//                        accessRecordCmd.insertOrUpdate(arbean, uid, CMD_ENUM.AR_INSERT);
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            });
        }
        room.setMember_ids(sendMemberIds);
        sendUtils.send(room, message, chatService);
    }


    public void sendAite(ChatTxtBean bean, String uid) {
        Message message = new Message();
        message.setBody(bean.getFromUid() + "#" + bean.getToGroupid() + "#" + uid + "#" + bean.getUuid() + "#" + bean.getFromName());
        message.setCMD(Message.CMD_ENUM.AITE.name());
        sendUtils.send(uid, message, chatService);
    }

    public void sendAite(ChatTxtBean bean) {
//		RoomBean room = ChatStore.ROOMB_BEAN_MAP.get(bean.getToGroupid());
        RoomBean room = chatStoreComponent.getRoomBeanMap(bean.getToGroupid());
        List<String> uids = Arrays.stream(room.getMember_ids().split("#")).distinct().collect(Collectors.toList());
        for (String uid : uids) {
            if (StringUtils.isEmpty(uid) || uid.equals(bean.getFromUid())) {
                continue;
            }
            /**不在线的不发*/
            if (!storeComponent.isMemberOnline(uid)) {
                continue;
            }
            MemberBean memberBean = chatStoreComponent.getMemberBean(uid);
            if (null == memberBean || "".equals(memberBean.getMember_type()) || memberBean.getMember_type() == Member.MEMBER_TYPE.ROBOT) {
                continue;
            }

            Message message = new Message();
            message.setBody(bean.getFromUid() + "#" + bean.getToGroupid() + "#" + uid + "#" + bean.getUuid() + "#" + bean.getFromName());
//			
            message.setCMD(Message.CMD_ENUM.AITE.name());
            sendUtils.send(uid, message, chatService);
        }
    }


}
