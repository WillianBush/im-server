package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.bean.ChatTxtBean;
import com.imservices.im.bmm.entity.EmployeeDefaultMessage;
import com.imservices.im.bmm.entity.Friends;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.service.AfterRegisterService;
import com.imservices.im.bmm.service.EmployeeDefaultMessageService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.websocket.cmd.UserChatCmd;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class AfterRegisterServiceImpl implements AfterRegisterService {


    private final MemberService memberService;
    private final UserChatCmd userChatCmd;
    private final EmployeeDefaultMessageService employeeDefaultMessageService;

    @Autowired
    public AfterRegisterServiceImpl(MemberService memberService, UserChatCmd userChatCmd, EmployeeDefaultMessageService employeeDefaultMessageService) throws Exception {
        this.memberService = memberService;
        this.userChatCmd = userChatCmd;
        this.employeeDefaultMessageService = employeeDefaultMessageService;
    }

    @Override
    public void afterRegister(Member user) throws Exception {
        // 通过邀请码注册的,需要给用户发员工默认设置的聊天问候语
        String[] ps = new String[]{"mid"};
        Object[] vs = new Object[]{user.getId()};
        List<Friends> friends = memberService.getFriendsList(ps, vs);
//        log.info("friods:{}", JSONObject.toJSONString(friends));
        for (Friends f : friends) {
            Member friend = memberService.get(f.getFriendid());
            if (friend == null || StringUtils.isEmpty(friend.getMemberId())) {
//                log.info("friend == null ,friend:{}", JSONObject.toJSONString(f));
                continue;
            }
            if (employeeDefaultMessageService.isEmployId(friend.getMemberId())) {
                CompletableFuture.runAsync(() -> {
                    this.sendHelloMessage(f.getFriendid(), f.getMid(), friend.getMemberId(), null );
                });
            }
        }
//        log.info("发送问候语结束,user:{}", JSONObject.toJSONString(user));
    }

    @Override
    public void afterRegister(Member user, String InviteCode) throws Exception {
        String[] ps = new String[]{"mid"};
        Object[] vs = new Object[]{user.getId()};
        List<Friends> friends = memberService.getFriendsList(ps, vs);
//        log.info("friods:{}", JSONObject.toJSONString(friends));
        for (Friends f : friends) {
            Member friend = memberService.get(f.getFriendid());
            if (friend == null || StringUtils.isEmpty(friend.getMemberId())) {
//                log.info("friend == null ,friend:{}", JSONObject.toJSONString(f));
                continue;
            }

            if (employeeDefaultMessageService.isEmployId(friend.getMemberId())) {
                CompletableFuture.runAsync(() -> {
                    this.sendHelloMessage(f.getFriendid(), f.getMid(), friend.getMemberId(),InviteCode);
                });
            }
        }
//        log.info("发送问候语结束,user:{}", JSONObject.toJSONString(user));
    }

    private void sendHelloMessage(String EmployeeId, String memberId, String employeeMemberId,String inviteCode) {
        try {
            EmployeeDefaultMessage defaultMessage = null;
            if (StringUtils.isEmpty(inviteCode)){
               List<EmployeeDefaultMessage> defaultMessages = employeeDefaultMessageService.getMyHelloMessage(employeeMemberId);
               if (!defaultMessages.isEmpty()){
                   defaultMessage = defaultMessages.get(0);
               }else {
                   return;
               }
            }else {
                defaultMessage = employeeDefaultMessageService.getMyHelloMessage(employeeMemberId,inviteCode);
            }

            if (null == defaultMessage){
                return;
            }


            ChatTxtBean bean = new ChatTxtBean();
            bean.setFromUid(EmployeeId);
            bean.setToUid(memberId);
            if (!StringUtils.isEmpty(defaultMessage.getMsg_1())) {
                Thread.sleep(5000);
                bean.setTxt(defaultMessage.getMsg_1());
                userChatCmd.sendTXT(bean);
                Thread.sleep(2000);
            }

            if (!StringUtils.isEmpty(defaultMessage.getPicture_1())) {
                userChatCmd.sengImag(bean, defaultMessage.getPicture_1());
                Thread.sleep(2000);
            }


            if (!StringUtils.isEmpty(defaultMessage.getMsg_2())) {
                bean.setTxt(defaultMessage.getMsg_2());
                userChatCmd.sendTXT(bean);
                Thread.sleep(2000);
            }


            if (!StringUtils.isEmpty(defaultMessage.getPicture_2())) {
                userChatCmd.sengImag(bean, defaultMessage.getPicture_2());
                Thread.sleep(2000);
            }


            if (!StringUtils.isEmpty(defaultMessage.getMsg_3())) {
                bean.setTxt(defaultMessage.getMsg_3());
                userChatCmd.sendTXT(bean);
                Thread.sleep(2000);
            }


            if (!StringUtils.isEmpty(defaultMessage.getPicture_3())) {
                userChatCmd.sengImag(bean, defaultMessage.getPicture_3());
                Thread.sleep(2000);
            }


            if (!StringUtils.isEmpty(defaultMessage.getPicture_4())) {
                userChatCmd.sengImag(bean, defaultMessage.getPicture_4());
                Thread.sleep(2000);
            }

            if (!StringUtils.isEmpty(defaultMessage.getPicture_5())) {
                userChatCmd.sengImag(bean, defaultMessage.getPicture_5());
            }

//            if (!StringUtils.isEmpty(inviteCode)) {
//                bean.setTxt("您好，请您下载安装好APP注册并输入邀请码注册,登陆后，就会有接待小姐姐为您安排约炮行程的哦，谢谢 。");
//                userChatCmd.sendTXT(bean);
//                Thread.sleep(2000);
//            }

        } catch (Exception e) {
            log.error("sendHelloMessage, EmployeeId:{}, memberId:{}", EmployeeId, memberId, e);
        }
    }

}
