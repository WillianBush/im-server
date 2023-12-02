package com.imservices.im.bmm.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.bean.ChatTxtBean;
import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.bean.store.ChatStore;
import com.imservices.im.bmm.bean.store.ChatStoreComponent;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.dao.BaseDAO;
import com.imservices.im.bmm.entity.*;
import com.imservices.im.bmm.entity.Member.MEMBER_TYPE;
import com.imservices.im.bmm.mq.RabbitmqConfig;
import com.imservices.im.bmm.service.*;
import com.imservices.im.bmm.utils.*;
import com.imservices.im.bmm.utils.oss.OssUtil;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.web.BeanUtils;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import com.imservices.im.bmm.utils.web.WebUtilsWeb;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Message.CMD_ENUM;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import com.imservices.im.bmm.websocket.cmd.UserChatCmd;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Controller("MemberJsonController")
@RequestMapping(value = {"/user/json", "visit"})
@AllArgsConstructor
@CrossOrigin
@Slf4j
public class MemberController {

    private MemberService memberService;
    private WebConfigService configService;
    private AccessRecordService accessRecordService;
    private TrajectoryService trajectoryService;
    private StoreComponent storeComponent;
    private IRUPService irupService;
    private RoomService roomService;
    private UserChatCmd userChatCmd;
    private ShimingService shimingService;
    private EmployeeService employeeService;

    private EmployeeDefaultMessageService employeeDefaultMessageService;
    private SendUtils sendUtils;
    private AfterRegisterService afterRegisterService;
    private RedisService redisService;
    private ChatStoreComponent chatStoreComponent;

    private H5Model h5Model;

    private OssUtil ossUtil;

    private Environment environment;

    private KefuService kefuService;


    private IpListService ipListService;


    @RequestMapping(value = "/verifySmsCode", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void verifySmsCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebConfig wc = configService.get();
        String tel = request.getParameter("tel");
        String code = request.getParameter("code");
        String TelCheckCode = (String) WebUtils.getSessionAttribute(request, "TelCheckCode");
        String sendSms_mobile = (String) WebUtils.getSessionAttribute(request, "sendSms_mobile");
        if (StringUtils.isEmpty(TelCheckCode) || StringUtils.isEmpty(sendSms_mobile)) {
            ResponseUtils.json(response, 500, "验证失败!", null);
            return;
        }
        if (!tel.equals(sendSms_mobile)) {
            ResponseUtils.json(response, 500, "获取验证码的手机不匹配", null);
            return;
        }
        if (!code.equals(TelCheckCode)) {
            ResponseUtils.json(response, 500, "验证码不正确", null);
            return;
        }
        String token = UUID.randomUUID().toString();
        request.getSession().setAttribute("FORGET_PAY_PWD_TOKEN", token);

        ResponseUtils.json(response, 200, token, null);
        return;

    }

    @AuthPassport
    @RequestMapping(value = "/shiming_info", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void shiming_info(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        Shiming shiming = shimingService.get("muuid", MEMBERID);
        if (null == shiming) {
            ResponseUtils.json(response, 500, "", null);
            return;
        }
        ResponseUtils.json(response, 200, shiming, null);
    }


    @AuthPassport
    @RequestMapping(value = "/shiming_update", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void shiming_update(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        Member member = memberService.get(MEMBERID);
        String realname = request.getParameter("realname");
        String idcard = request.getParameter("idcard");
        Shiming shiming = shimingService.get("muuid", MEMBERID);
        if (null == shiming) {
            shiming = new Shiming();
            shiming.setMid(member.getMemberId());
            shiming.setMuuid(member.getId());
            shimingService.save(shiming);
        } else {
            if (shiming.getStatus().intValue() == 0) {
                ResponseUtils.json(response, 500, "审核中,请忽重复提交", null);
                return;
            }
        }
        shiming.setRealname(realname);
        shiming.setIdcard(idcard);
        shiming.setStatus(0);
        shiming.setStatusReason("");
        shiming.setStatusDate(null);
        shiming.setCreateDate(new Date());
        shimingService.update(shiming);
        member.setShimingStatus(0);
        memberService.update(member);
//        memberService.update(new String[]{"shimingStatus"}, new Object[]{0}, "where id='" + MEMBERID + "'");
        MemberBean mb = chatStoreComponent.getMemberBean(MEMBERID);
        if (!ObjectUtils.isEmpty(mb)) {
            mb.setShimingStatus(0);
            chatStoreComponent.putMemberBean(MEMBERID, mb);
            chatStoreComponent.putMemberBean(MEMBERID, mb);
        }
        ResponseUtils.json(response, 200, shiming, null);
    }

    @AuthPassport
    @RequestMapping(value = "/isSuperUser", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void isSuperUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        Member member = memberService.get(MEMBERID);
        WebConfig wc = configService.get();
        int flag = 0;
        if (!StringUtils.isEmpty(wc.getSuperUser()) && wc.getSuperUser().indexOf(member.getMemberId()) >= 0) {
            flag = 1;
        }
        ResponseUtils.json(response, 200, flag + "", null);
    }


    @AuthPassport
    @RequestMapping(value = "/paypwdCheck", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void paypwdCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pay_pwd = request.getParameter("pay_pwd");
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        Member member = memberService.get(MEMBERID);

        if (!StringUtils.isEmpty(member.getPay_pwd()) && MD5.MD5Encode(pay_pwd).equals(member.getPay_pwd())) {
            ResponseUtils.json(response, 200, "1", null);
        } else {
            ResponseUtils.json(response, 200, "0", null);
        }
    }

    @AuthPassport
    @RequestMapping(value = "/hasPaypwd", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void hasPaypwd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        Member member = memberService.get(MEMBERID);
        if (!StringUtils.isEmpty(member.getPay_pwd())) {
            ResponseUtils.json(response, 200, "1", null);
        } else {
            ResponseUtils.json(response, 200, "0", null);
        }

    }

    @AuthPassport
    @RequestMapping(value = "/updatePayPwd", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void updatePayPwd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String paypwd = request.getParameter("paypwd");
        if (StringUtils.isEmpty(paypwd)) return;
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        Member member = memberService.get(MEMBERID);


        String forget_token = request.getParameter("forget_token");
        if (!StringUtils.isEmpty(forget_token)) {
            String FORGET_PAY_PWD_TOKEN = (String) request.getSession().getAttribute("FORGET_PAY_PWD_TOKEN");

//			if(StringUtils.isEmpty(FORGET_PAY_PWD_TOKEN) || !forget_token.equals(FORGET_PAY_PWD_TOKEN)) {
//				ResponseUtils.json(response,500,"错误",null);
//				return;
//			}

            request.getSession().removeAttribute("FORGET_PAY_PWD_TOKEN");
        } else {
            if (!StringUtils.isEmpty(member.getPay_pwd())) {
                String paypwd_old = request.getParameter("paypwd_old");
                if (!MD5.MD5Encode(paypwd_old).equals(member.getPay_pwd())) {
                    ResponseUtils.json(response, 500, "原支付密码错误", null);
                    return;
                }
            }
        }

//        memberService.update(new String[]{"pay_pwd"}, new Object[]{MD5.MD5Encode(paypwd)}, "where id='" + member.getId() + "'");

        ResponseUtils.json(response, 200, "", null);
    }


    @AuthPassport
    @RequestMapping(value = "/getBlackList", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void getBlackList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        //List<Blacklist> list1 = memberService.getBlackList(new String[]{"mid"}, new Object[]{MEMBERID});
        List<MemberBean> list = new ArrayList<MemberBean>();
        //if(!list1.isEmpty()) {
        String uids = storeComponent.getBlackList(MEMBERID);
        if (!StringUtils.isEmpty(uids)) {
            String[] arrs = uids.split(",");
            for (String o : arrs) {
                if (StringUtils.isEmpty(o)) continue;
                MemberBean mb = storeComponent.getMemberBeanFromMapDB(o);
                MemberBean bean = new MemberBean();
                bean.setHeadpic(mb.getHeadpic());
                bean.setId(mb.getId());
                bean.setNickName(mb.getNickName());
                list.add(bean);
            }
        }
        //}
        ResponseUtils.json(response, 200, list, null);
    }


    @AuthPassport
    @RequestMapping(value = "/isBlack", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void isBlack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = request.getParameter("uid");
        if (StringUtils.isEmpty(uid)) return;
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);

        //Long l = memberService.blackListCount(new String[]{"mid","blacklist_ids"}, new Object[]{MEMBERID,"%"+uid+"%"});
        if (storeComponent.getBlackList(MEMBERID).indexOf(uid) < 0) {
            ResponseUtils.json(response, 200, "0", null);
        } else {
            ResponseUtils.json(response, 200, "1", null);
        }
    }

    @AuthPassport
    @RequestMapping(value = "/removeBlack", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void removeBlack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = request.getParameter("uid");
        if (StringUtils.isEmpty(uid)) return;
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);

        List<Blacklist> list = memberService.getBlackList(new String[]{"mid"}, new Object[]{MEMBERID});
        if (!list.isEmpty()) {
            Blacklist o = list.get(0);
            o.setBlacklist_ids(o.getBlacklist_ids().replaceAll(uid + ",", ""));
            memberService.updateBlackList(o);

            String v = storeComponent.getBlackList(MEMBERID);
            ChatStore.BLACK_LIST.put(MEMBERID, o.getBlacklist_ids());
        }
        ResponseUtils.json(response, 200, "0", null);
    }

    @AuthPassport
    @RequestMapping(value = "/addBlack", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void addBlack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = request.getParameter("uid");
        if (StringUtils.isEmpty(uid)) return;
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);

        List<Blacklist> list = memberService.getBlackList(new String[]{"mid"}, new Object[]{MEMBERID});
        if (list.isEmpty()) {
            Blacklist o = new Blacklist();
            o.setMid(MEMBERID);
            o.setBlacklist_ids(uid + ",");
            memberService.saveBlackList(o);
            ChatStore.BLACK_LIST.put(MEMBERID, o.getBlacklist_ids());
        } else {
            Blacklist o = list.get(0);
            o.setBlacklist_ids(o.getBlacklist_ids() + uid + ",");
            memberService.updateBlackList(o);
            ChatStore.BLACK_LIST.put(MEMBERID, o.getBlacklist_ids());
        }
        ResponseUtils.json(response, 200, "0", null);
    }


    @AuthPassport
    @RequestMapping(value = "/room/list", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void room_list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        List<Room> list = roomService.getList(new String[]{"member_ids"}, new Object[]{"%" + MEMBERID + "%"});
        List<RoomBean> list1 = new ArrayList<RoomBean>();
        for (Room r : list) {
            RoomBean mb = BeanUtils.roomToBeanSimple(r);
            list1.add(mb);
        }
        ResponseUtils.json(response, 200, list1, null);
    }


    @AuthPassport
    @RequestMapping(value = {"/bindNewTel/v1"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void bindNewTel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        Member member = memberService.get(MEMBERID);
        String tel = request.getParameter("tel");
        String code = request.getParameter("code");
        if (StringUtils.isEmpty(tel)) {
            ResponseUtils.json(response, 500, "新手机号不能为空!", null);
            return;
        }
        if (!ValidateUtil.instance().validatePhone(tel)) {
            ResponseUtils.json(response, 500, "手机号码格式不正确!", null);
            return;
        }

        String TelCheckCode = (String) WebUtils.getSessionAttribute(request, "TelCheckCode");
        String sendSms_mobile = (String) WebUtils.getSessionAttribute(request, "sendSms_mobile");

        if (StringUtils.isEmpty(TelCheckCode) || StringUtils.isEmpty(sendSms_mobile)) {
            ResponseUtils.json(response, 500, "验证失败!", null);
            return;
        }
        if (!tel.equals(sendSms_mobile)) {
            ResponseUtils.json(response, 500, "获取验证码的手机不匹配", null);
            return;
        }
        if (!code.equals(TelCheckCode)) {
            ResponseUtils.json(response, 500, "验证码不正确", null);
            return;
        }
        Long l = memberService.count(new String[]{"telphone"}, new Object[]{tel});
        member.setTelphone(tel);
        if (null != l && l > 0) {
            ResponseUtils.json(response, 500, "手机已存在", null);
            return;
        } else {
            member.setTelphone(tel);
            memberService.update(member);
//            memberService.update(new String[]{"telphone", "username"}, new Object[]{tel, tel}, "where id='" + MEMBERID + "'");

            WebUtils.setSessionAttribute(request, "TelCheckCode", null);
            WebUtils.setSessionAttribute(request, "sendSms_mobile", null);
            ResponseUtils.json(response, 200, "", null);
            trajectoryService.generate(storeComponent.getMemberBeanFromMapDB(MEMBERID), "绑定新手机:" + tel);
        }


    }

    @AuthPassport
    @RequestMapping(value = {"/checkTelSmsCode/v1"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void checkTelSmsCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tel = request.getParameter("tel");
        String code = request.getParameter("code");
        if (StringUtils.isEmpty(tel)) {
            ResponseUtils.json(response, 500, "手机号不能为空!", null);
            return;
        }
        if (!ValidateUtil.instance().validatePhone(tel)) {
            ResponseUtils.json(response, 500, "手机号码格式不正确!", null);
            return;
        }

        String TelCheckCode = (String) WebUtils.getSessionAttribute(request, "TelCheckCode");
        String sendSms_mobile = (String) WebUtils.getSessionAttribute(request, "sendSms_mobile");

        if (StringUtils.isEmpty(TelCheckCode) || StringUtils.isEmpty(sendSms_mobile)) {
            ResponseUtils.json(response, 500, "验证失败!", null);
            return;
        }
        if (!tel.equals(sendSms_mobile)) {
            ResponseUtils.json(response, 500, "获取验证码的手机不匹配", null);
            return;
        }
        if (!code.equals(TelCheckCode)) {
            ResponseUtils.json(response, 500, "验证码不正确", null);
            return;
        }

        WebUtils.setSessionAttribute(request, "TelCheckCode", null);
        WebUtils.setSessionAttribute(request, "sendSms_mobile", null);
        ResponseUtils.json(response, 200, "", null);
    }


    @RequestMapping(value = {"/sendSms/v1"}, method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void sendSmsCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebConfig wc = configService.get();
        if (WebUtils.getSessionAttribute(request, "mobileCodeTime") != null) {
            Date d1 = (Date) WebUtils.getSessionAttribute(request, "mobileCodeTime");
            Date d2 = new Date();
            Long deep = Long.valueOf((d2.getTime() - d1.getTime()) / 1000L);
            if (deep.longValue() <= 60L) {
                ResponseUtils.json(response, 500, "请" + (120L - deep.longValue()) + "秒后再尝试!", null);
                return;
            }
        }
        String tel = request.getParameter("tel");

        if (StringUtils.isEmpty(tel)) {
            ResponseUtils.json(response, 500, "手机号不能为空!", null);
            return;
        }
        if (!ValidateUtil.instance().validatePhone(tel)) {
            ResponseUtils.json(response, 500, "手机号码格式不正确!", null);
            return;
        }
        Long rand = Long.valueOf(Math.round(Math.random() * 1000000.0D));

//	    String msg = "";
//	    if(!StringUtils.isEmpty(wc.getSmsContent())) {
//	    	msg = wc.getSmsContent();
//	    }
//	    msg = msg.replaceAll("#code#", rand.toString());

        try {
            SmsUtil.sendAuthCode(tel, rand.toString(), wc);
//		      SmsUtil.send(tel, msg,wc.getSmsApiKey()); 
            WebUtils.setSessionAttribute(request, "TelCheckCode", rand.toString());
            WebUtils.setSessionAttribute(request, "sendSms_mobile", tel);
            WebUtils.setSessionAttribute(request, "mobileCodeTime", new Date());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtils.json(response, 500, "验证码发送失败!", null);
            return;
        }
        ResponseUtils.json(response, 200, "验证码已成功发送!", null);
    }


    @RequestMapping(value = "/getBySession/v1", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void getBySession_v1(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        //
        if (StringUtils.isEmpty(MEMBERID)) {
            ResponseUtils.json(response, 200, null, null);
            return;
        }
        Member member = memberService.get(MEMBERID);
        if (null == member) {
            ResponseUtils.json(response, 200, null, null);
            return;
        }
        ResponseUtils.json(response, 200, BeanUtils.memberToBeanSimple(member), null);
    }

    @RequestMapping(value = "/getBySessionFromMapStore/v1", method = {RequestMethod.GET})
    public void getBySessionFromMapStore_v1(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        //
        if (StringUtils.isEmpty(MEMBERID)) {
            //
            ResponseUtils.json(response, 200, null, null);
            return;
        }
        MemberBean bean = storeComponent.getMemberBeanFromMapDB(MEMBERID);
        if (null == bean) {
            //
            ResponseUtils.json(response, 200, null, null);
            return;
        }
        //
        ResponseUtils.json(response, 200, bean, null);
    }


    @AuthPassport
    @RequestMapping(value = "/money/v1", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void memberMoney_v1(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        MemberBean member = storeComponent.getMemberBeanFromMapDB(MEMBERID);
        DecimalFormat df = new DecimalFormat("0.##");
        //
        ResponseUtils.json(response, 200, df.format(member.getMoney()), null);
    }


    @AuthPassport
    @RequestMapping(value = "/money4Room/v1", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void money4Room_v1(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        String roomUUID = request.getParameter("roomUUID");
//        RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(roomUUID);
        RoomBean rb = chatStoreComponent.getRoomBeanMap(roomUUID);
        DecimalFormat df = new DecimalFormat("0.###");
        if (rb.getIndependence() == 1) {
            List<IndependentRoomUserPrice> list = irupService.getList(new String[]{"room_uuid", "user_uuid"}, new Object[]{roomUUID, MEMBERID});
            if (!list.isEmpty()) {
                IndependentRoomUserPrice irup = list.get(0);
                ResponseUtils.json(response, 200, df.format(irup.getPrice()), null);
            } else {
                ResponseUtils.json(response, 200, "0.0", null);
            }
        } else {
            Member member = memberService.get(MEMBERID);
            ResponseUtils.json(response, 200, df.format(member.getMoney()), null);
        }


    }


    @AuthPassport
    @RequestMapping(value = "/friendDetail/v1", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void friendDetail_v1(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id)) {
            ResponseUtils.json(response, 500, "用户不存在", null);
            return;
        }
        Member member = memberService.get(id);
        if (null == member) {
            ResponseUtils.json(response, 500, "用户不存在", null);
            return;
        }
        MemberBean mb = BeanUtils.memberToBeanSimple(member);
        ResponseUtils.json(response, 200, mb, null);
    }


    @AuthPassport
    @RequestMapping(value = "/logout", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String act = request.getParameter("act");
        String xAccessClient = (String) request.getSession().getAttribute("x-access-client");
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, null);
        request.getSession().removeAttribute(MemberConstant.MEMBERIDSESSION);

        MemberBean mb = storeComponent.getMemberBeanFromMapDB(MEMBERID);
        if (null != mb && !StringUtils.isEmpty(MEMBERID)) {
//            ChatStore.USER_BEAN_MAP.remove(MEMBERID);
            chatStoreComponent.delMemberBean(MEMBERID);
            ChatStore.USER_APPPUSH.remove(MEMBERID);//退出帐号后，APP离线也不会被推送消息

            /**旧代码MU**/
//            WebSocketSession ws1 = SessionStore.USERID_WS_MAP_GET_ByKey(mb.getId() + "#" + xAccessClient);
//            if (null != ws1) SessionStore.WS_USERID_MAP.remove(ws1);
//            SessionStore.USERID_WS_MAP_REMOVE(mb.getId() + "#" + xAccessClient);
            SessionStore.USERID_WS_MAP_GET_ByKey(mb.getId() + "#" + xAccessClient).forEach(ws -> {
                SessionStore.SESSIONID_MEMBERID_LIST_MAP.remove(ws.getId());
            });
            /**全部退出，todo 后期改成前端传入sessionid，根据id进行删除*/
            SessionStore.USERID_SESSION_WS_MAP.remove(mb.getId() + "#" + xAccessClient);
            /**未发现这样存入 将缓存在redis的请求地址删除**/
            redisService.delete(mb.getId() + "#" + xAccessClient);
            redisService.hDelete(SessionStore.REDIS_WSS_KEY, mb.getId().split("#")[0]);
            redisService.hDelete(SessionStore.ONLINE_MEMBER, mb.getId() + "#" + xAccessClient);
            if (!StringUtils.isEmpty(act) && "OTHER_LOGIN".equals(act)) {
                //
                //如果是冲线退出的，不需要执行以下代码。因为在ChatCoreWebSocketHandler.class类处理冲线的情况已经把旧的移除
                //这样做的目的是为了防止，那边添加了。这里就移除了
                trajectoryService.generate(mb, "账号被冲线退出");
            } else {
                trajectoryService.generate(mb, "退出游戏(H5)");
            }

            //上线通知，我上线了
            List<AccessRecord> arlist = accessRecordService.getList(new String[]{"entityid"}, new Object[]{mb.getId()});
            if (!arlist.isEmpty()) {
                for (AccessRecord o : arlist) {
                    if (!ObjectUtils.isEmpty(chatStoreComponent.getMemberBean(o.getUid()))) {
                        List<WebSocketSession> ws_list = SessionStore.USERID_WS_MAP_GET_ByUid(o.getUid());
                        for (WebSocketSession ws : ws_list) {
                            Message msg = new Message();
                            msg.setCMD(CMD_ENUM.FRIEND_OFFLINE.toString());
                            msg.setBody(o.getEntityid());
                            sendUtils.send(ws, msg);
                        }
                    }
                }
            }
            //如果我在某个超级用户聊天聊天中，则通知对方我的状态
            Iterator<String> it = ChatStore.ONLINE_SUPER_USER.keySet().iterator();
            while (it.hasNext()) {
                String super_user_id = it.next();
                Long l = accessRecordService.count(new String[]{"uid", "entityid"}, new Object[]{super_user_id, mb.getId()});
                if (null != l && l > 0) {
                    Message msg = new Message();
                    msg.setCMD(CMD_ENUM.FRIEND_OFFLINE.toString());
                    msg.setBody(mb.getId());
                    sendUtils.send(super_user_id, msg);
                }
            }

            //如果是超级用户即删除在线
            ChatStore.ONLINE_SUPER_USER.remove(mb.getId());


        }
        ResponseUtils.json(response, 200, "", null);
    }


    @AuthPassport
    @RequestMapping(value = "/repwd", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void repwd(String oldpwd, String newpwd, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(oldpwd) || StringUtils.isEmpty(newpwd)) {
            ResponseUtils.json(response, 500, "请输入原密码或新密码", null);
            return;
        }
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        try {
            Member member = memberService.get(MEMBERID);
            if (!MD5.MD5Encode(oldpwd).equals(member.getPassword())) {
                ResponseUtils.json(response, 500, "原密码错误", null);
                return;
            } else {
                member.setPassword(MD5.MD5Encode(newpwd));
            }

            memberService.update(member);
            trajectoryService.generate(BeanUtils.memberToBeanSimple(member), "修改了密码 IP:" + WebUtilsWeb.getIpAddr(request));
        } catch (Exception e) {
            log.error("", e);
        }


        ResponseUtils.json(response, 200, "", null);
    }

    @AuthPassport
    @RequestMapping(value = "/rename", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void rename(String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);

//        MemberBean member = ChatStore.USER_BEAN_MAP.get(MEMBERID);
        MemberBean member = chatStoreComponent.getMemberBean(MEMBERID);
        Member mb = memberService.get(member.getId());
        if (null == member) {
            member = BeanUtils.memberToBeanSimple(memberService.get(MEMBERID));
            if (null == member)
                return;
        }
        Long l = memberService.count(new String[]{"nickName", "memberId"}, new Object[]{name, BaseDAO.NOTEQUALS + member.getMemberId()});
        if (null != l && l > 0) {
            ResponseUtils.json(response, 500, "昵称已被使用", null);
        } else {
            member.setNickName(name);
            memberService.update(mb);
//            memberService.update(new String[]{"nickName"}, new Object[]{member.getNickName()}, "where id='" + member.getId() + "'");

            ChatStore.modifyMemberBeanProperties(member.getId(), (Map) ImmutableMap.of("nickName", member.getNickName()));
            accessRecordService.updateByEid(member.getId(), new String[]{"title"}, new String[]{member.getNickName()});
//            trajectoryService.generate(member, "修改了昵称");
            ResponseUtils.json(response, 200, "", null);
        }
    }


    @RequestMapping(value = "/i", method = {RequestMethod.GET})
    public void i(@RequestParam Integer u, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //idpeRoomUUID
        String idpeRoomUUID = request.getParameter("idpeRoomUUID");
        if (!StringUtils.isEmpty(idpeRoomUUID)) {
            Room room = roomService.get(idpeRoomUUID);
            if (null != room && room.getIndependence() == 1) {
                request.getSession().setAttribute("IndependenceRoomUUID", room.getId());//用于作为已进入独立房间的标记
            }
        }

//		http://ceshi888.nuvdy.cn/user/json/i?u=1688&idpeRoomUUID=2c9a9c4772a88b820172c2b4be561f46

        request.getSession().setAttribute("$invitor", u);
        response.sendRedirect("/#/register_1");


    }

    @RequestMapping(value = "/register", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void register(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebConfig wc = configService.get();

        if (!StringUtils.isEmpty(wc.getForbidRegister4ip()) &&
                wc.getForbidRegister4ip().indexOf(WebUtilsWeb.getIpAddr(request)) >= 0) {
            ResponseUtils.json(response, 500, "注册失败", null);
            return;
        }

        //String account = request.getParameter("account");
        String tel = request.getParameter("tel");
        String nickname = request.getParameter("nickname");
        String headpic = request.getParameter("headpic");

        String password = request.getParameter("password");
        Long l = memberService.count(new String[]{"telphone"}, new Object[]{tel});
        if (null != l && l > 0) {
            ResponseUtils.json(response, 500, "手机已存在", null);
            return;
        }

        l = memberService.count(new String[]{"nickName"}, new Object[]{nickname});
        if (null != l && l > 0) {
            ResponseUtils.json(response, 500, "昵称已存在", null);
            return;
        }


        if (wc.getReg_sms().intValue() == 1) {
            String code = request.getParameter("code");
            String TelCheckCode = (String) WebUtils.getSessionAttribute(request, "TelCheckCode");
            String sendSms_mobile = (String) WebUtils.getSessionAttribute(request, "sendSms_mobile");
            if (StringUtils.isEmpty(TelCheckCode) || StringUtils.isEmpty(sendSms_mobile)) {
                ResponseUtils.json(response, 500, "验证失败!", null);
                return;
            }
            if (!tel.equals(sendSms_mobile)) {
                ResponseUtils.json(response, 500, "获取验证码的手机不匹配", null);
                return;
            }
            if (!code.equals(TelCheckCode)) {
                ResponseUtils.json(response, 500, "验证码不正确", null);
                return;
            }

        }


        if (wc.getUseRegisterCode().intValue() == 1 && !StringUtils.isEmpty(wc.getRegisterCode())) {
            String regCode = request.getParameter("regCode");
            if (StringUtils.isEmpty(regCode) || !regCode.equals(wc.getRegisterCode())) {
                ResponseUtils.json(response, 500, "注册码不正确", null);
                return;
            }
        }

        Member member = new Member();

        member.setMemberType(MEMBER_TYPE.USER);
        member.setUsername(tel);
        member.setTelphone(tel);
        member.setPassword(MD5.MD5Encode(password));
        member.setRegistIp(WebUtilsWeb.getIpAddr(request));
        if (!StringUtils.isEmpty(nickname)) {
            member.setNickName(nickname);
        } else {
            member.setNickName(tel);
        }
        member.setCreateDate(new Date());
        String redisKeyMember = "memberId";
        Long memberNumber = redisService.incrBy(redisKeyMember, 1L);
        member.setMemberId(memberNumber.toString());
        member.setStatus(0);
        member.setLastLoginDate(new Date());
        member.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
        if (!StringUtils.isEmpty(headpic)) {
            member.setHeadpic(headpic);
        } else {
            member.setHeadpic("/img_sys/defaultHeadPic.jpg");
        }
        memberService.save(member);


        request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, member.getId());

        MemberBean mb = new MemberBean();
        mb.setParent_uuid(member.getParent());
        mb.setMember_type(member.getMemberType());
        mb.setId(member.getId());
        if (StringUtils.isEmpty(member.getHeadpic())) {
            mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
        } else {
            mb.setHeadpic(member.getHeadpic());
        }
        mb.setPreTxImg(member.getPreTxImg());
        mb.setPreTxPay(member.getPreTxPay());

        mb.setLastLoginDate(member.getLastLoginDate());
        mb.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
        mb.setLosePriceSum(member.getLosePriceSum());
        mb.setMemberId(member.getMemberId());
        mb.setMoney(member.getMoney());
        mb.setUsername(member.getUsername());
        mb.setNickName(member.getNickName());
        mb.setOpenRedCount(member.getOpenRedCount());
        mb.setOpenRedPriceSum(member.getOpenRedPriceSum());
        mb.setParent(member.getParent());
        mb.setRechargePriceSum(member.getRechargePriceSum());
        mb.setRegistIp(member.getRegistIp());
        mb.setSendRedCount(member.getSendRedCount());
        mb.setSendRedPriceSum(member.getSendRedPriceSum());
        mb.setStatus(member.getStatus());
        mb.setTelphone(member.getTelphone());
        mb.setTxPriceSum(member.getTxPriceSum());
        mb.setTxMoneyIng(member.getTxMoneyIng());
        mb.setWinPriceSum(member.getWinPriceSum());
        mb.setTichenPriceSum(member.getTichenPriceSum());
        mb.setQrCodeImg(member.getQrCodeImg());
        mb.setPreBank_addr(member.getPreBank_addr());
        mb.setPreBank_belonger(member.getPreBank_belonger());
        mb.setPreBank_code(member.getPreBank_code());
        mb.setPreBank_name(member.getPreBank_name());

        String[] ps = new String[]{"friendid", "status"};
        Object[] vs = new Object[]{member.getId(), FriendsAdd.Status.wait};
        l = memberService.friendsAddCount(ps, vs);
        mb.setUnDoFriendAddCount(l);

        ps = new String[]{"room_owner_id", "status"};
        vs = new Object[]{member.getId(), RoomAdd.Status.wait};
        l = roomService.roomAddCount(ps, vs);
        mb.setUnDoRoomAddCount(l);

        MemberLoginLog log = new MemberLoginLog();
        log.setCreateDate(new Date());
        log.setIpAddr(WebUtilsWeb.getIpAddress(WebUtilsWeb.getIpAddr(request)));
        log.setIp(WebUtilsWeb.getIpAddr(request));
        log.setMid(member.getMemberId());
        log.setMnickName(member.getNickName());
        log.setMtel(member.getTelphone());
        memberService.saveLoginLog(log);

        chatStoreComponent.putMemberBean(member.getId(), BeanUtils.memberToBeanSimple(member));

        WebUtils.setSessionAttribute(request, "TelCheckCode", null);
        WebUtils.setSessionAttribute(request, "sendSms_mobile", null);


        //员工邀请码
        if (wc.getUseInviteCode().intValue() == 1) {
            String inviteCode = request.getParameter("inviteCode");
            if (StringUtils.isEmpty(inviteCode)) {
                ResponseUtils.json(response, 500, "邀请码不正确", null);
                return;
            }
            Employee e = employeeService.get("inviteCode", inviteCode);
            if (null == e) {
                ResponseUtils.json(response, 500, "邀请码不正确", null);
                return;
            }
            MemberBean mb1 = storeComponent.getMemberBeanFromMapDB(e.getMember_uuid());


            Friends f = new Friends();
            f.setCreateDate(new Date());
            f.setFriendid(mb1.getId());
            f.setMid(member.getId());
            memberService.saveFriends(f);


            f = new Friends();
            f.setCreateDate(new Date());
            f.setFriendid(member.getId());
            f.setMid(mb1.getId());
            memberService.saveFriends(f);

            ChatTxtBean bean = new ChatTxtBean();
            bean.setFromUid(member.getId());
            bean.setToUid(mb1.getId());
            bean.setTxt("我们已经成为好友了，现在可以开始聊天了！");
            userChatCmd.sendTXT(bean);

        }


        MemberBean m = storeComponent.getMemberBeanFromMapDB("-1");//官方团队
        //添加官方团队好友
        Friends f = new Friends();
        f.setCreateDate(new Date());
        f.setFriendid(m.getId());
        f.setMid(member.getId());
        memberService.saveFriends(f);

        if (!StringUtils.isEmpty(wc.getNewRegAddFriends())) {
            String[] rs = wc.getNewRegAddFriends().split(",");
            for (String r : rs) {
                if (StringUtils.isEmpty(r)) continue;
                Member fri = memberService.get("memberId", r);
                if (null == fri) continue;
                f = new Friends();
                f.setCreateDate(new Date());
                f.setFriendid(fri.getId());
                f.setMid(member.getId());
                memberService.saveFriends(f);
                f = new Friends();
                f.setCreateDate(new Date());
                f.setFriendid(member.getId());
                f.setMid(fri.getId());
                memberService.saveFriends(f);

                if (!StringUtils.isEmpty(wc.getNewRegAddFriends_welcomeWord())) {
                    //
                    ChatTxtBean bean = new ChatTxtBean();
                    bean.setFromUid(fri.getId());
                    bean.setToUid(member.getId());
                    //bean.setTxt("您被【"+member.getNickName()+"】邀请加入【"+room.getName()+"】群组");
                    bean.setTxt(wc.getNewRegAddFriends_welcomeWord());
                    userChatCmd.sendTXT(bean);
                    //
                }


                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(5000);
                        ChatTxtBean bean = new ChatTxtBean();
                        bean.setFromUid(member.getId());
                        bean.setToUid(fri.getId());
                        bean.setTxt("我们已经成为好友了，现在可以开始聊天了！");
                        userChatCmd.sendTXT(bean);
                        afterRegisterService.afterRegister(member);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }


        if (!StringUtils.isEmpty(wc.getWelcomeStr())) {
            ChatTxtBean bean = new ChatTxtBean();
            bean.setFromUid("-1");
            bean.setToUid(member.getId());
            bean.setTxt(wc.getWelcomeStr());
            userChatCmd.sendTXT(bean);
        }

        ResponseUtils.json(response, 200, mb, null);
//			trajectoryService.generate(mb, "注册并登陆了游戏");
    }

    @RequestMapping(value = "/checkAndUpdateSession", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void checkAndUpdateSession(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = request.getParameter("uid");
//		//
//		//
        if (null == request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION)) {
            request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, uid);
        }
    }

    @RequestMapping(value = "/login", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            ResponseUtils.json(response, 500, "账号或密码错误", null);
            return;
        }
        List<Member> list = memberService.getList(new String[]{"telphone", "password"}, new Object[]{account, new MD5().MD5Encode(password)});
        if (null == list || list.isEmpty()) {
            list = memberService.getList(new String[]{"username", "password"}, new Object[]{account, new MD5().MD5Encode(password)});
        }
        if (null == list || list.isEmpty()) {
            ResponseUtils.json(response, 500, "手机/帐号或密码错误", null);
        } else {
            Member member = list.get(0);
            if (null != member.getStatus() && member.getStatus().intValue() == 1) {
                ResponseUtils.json(response, 500, "帐号禁用", null);
                return;
            }

            if (member.getMemberType() == MEMBER_TYPE.ROBOT) {
                ResponseUtils.json(response, 500, "该账号类型禁止登录", null);
                return;
            }
            /**
             * 特权用户
             */
            Random rd = new Random();
//            member.setId(member.getId()+rd.nextInt(100));
            request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, member.getId());
            MemberBean mb = new MemberBean();
            mb.setParent_uuid(member.getParent());
            mb.setMember_type(member.getMemberType());
            mb.setId(member.getId());
            mb.setUrl("http://" + configService.get().getWebsiteUrl() + "/user/json/i?u=" + member.getMemberId());
            if (StringUtils.isEmpty(member.getHeadpic())) {
                mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
            } else {
                mb.setHeadpic(member.getHeadpic());
            }
            mb.setPreTxImg(member.getPreTxImg());
            mb.setSex(member.getSex());
            mb.setPreTxPay(member.getPreTxPay());
            mb.setLastLoginDate(member.getLastLoginDate());
            mb.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
            mb.setLosePriceSum(member.getLosePriceSum());
            mb.setMemberId(member.getMemberId());
            mb.setMoney(member.getMoney());
            mb.setUsername(member.getUsername());
            mb.setNickName(member.getNickName());
            mb.setOpenRedCount(member.getOpenRedCount());
            mb.setOpenRedPriceSum(member.getOpenRedPriceSum());
            mb.setParent(member.getParent());
            mb.setRechargePriceSum(member.getRechargePriceSum());
            mb.setRegistIp(member.getRegistIp());
            mb.setSendRedCount(member.getSendRedCount());
            mb.setSendRedPriceSum(member.getSendRedPriceSum());
            mb.setStatus(member.getStatus());
            mb.setTelphone(member.getTelphone());
            mb.setPreBank_addr(member.getPreBank_addr());
            mb.setPreBank_belonger(member.getPreBank_belonger());
            mb.setPreBank_code(member.getPreBank_code());
            mb.setPreBank_name(member.getPreBank_name());
            mb.setTxPriceSum(member.getTxPriceSum());
            mb.setTxMoneyIng(member.getTxMoneyIng());
            mb.setWinPriceSum(member.getWinPriceSum());
            mb.setTichenPriceSum(member.getTichenPriceSum());
            String ossPath = "/img_sys/upload/member";

            member.setLastLoginDate(new Date());
            member.setLastLoginIp(WebUtilsWeb.getIpAddr(request));

            mb.setQrCodeImg(member.getQrCodeImg());

            String[] ps = new String[]{"friendid", "status"};
            Object[] vs = new Object[]{member.getId(), FriendsAdd.Status.wait};
            Long l = memberService.friendsAddCount(ps, vs);
            mb.setUnDoFriendAddCount(l);

            ps = new String[]{"room_owner_id", "status"};
            vs = new Object[]{member.getId(), RoomAdd.Status.wait};
            l = roomService.roomAddCount(ps, vs);
            mb.setUnDoRoomAddCount(l);


            MemberLoginLog log = new MemberLoginLog();
            log.setCreateDate(new Date());
            log.setIpAddr(WebUtilsWeb.getIpAddress(WebUtilsWeb.getIpAddr(request)));
            log.setIp(WebUtilsWeb.getIpAddr(request));
            log.setMid(member.getMemberId());
            log.setMnickName(member.getNickName());
            log.setMtel(member.getTelphone());
            memberService.saveLoginLog(log);


            chatStoreComponent.putMemberBean(member.getId(), BeanUtils.memberToBeanSimple(member));

            trajectoryService.generate(mb, "登陆了游戏(H5)");
            Object independenceRoomUUID = request.getSession().getAttribute("IndependenceRoomUUID");
            if (null == independenceRoomUUID || StringUtils.isEmpty(independenceRoomUUID.toString())) {
                ResponseUtils.json(response, 200, mb, null);
            } else {
//                RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(independenceRoomUUID.toString());
                RoomBean rb = chatStoreComponent.getRoomBeanMap(independenceRoomUUID.toString());
                if (rb.getIndependence() == 1) {
                    ResponseUtils.json(response, 200, mb, "/#/group/chat/" + independenceRoomUUID);
                } else {
                    ResponseUtils.json(response, 200, mb, null);
                }
            }
        }
    }


    @ResponseBody
    @RequestMapping(value = "/updateHeadPic", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public String updateHeadPic(HttpServletRequest request) throws Exception {
        String u = request.getParameter("u");
        if (StringUtils.isEmpty(u)) return "";
        //Member member = memberService.get(u);
        MemberBean member = chatStoreComponent.getMemberBean(u);
        if (null == member) {
            member = BeanUtils.memberToBeanSimple(memberService.get(u));
            if (null == member) return null;
        }
        String file = request.getParameter("file");
        String filename = ImageUtil.BASE64ImageSaveTo(file, UUID.randomUUID().toString().replaceAll("-", "") + ".png",
                "images/upload/member/", request);
        if (!StringUtils.isEmpty(filename)) {
            if (!StringUtils.isEmpty(member.getHeadpic())) {
                //FileProcess.del(request.getRealPath(member.getHeadpic()));

                FTPUtil.deleteFile("img_member", member.getHeadpic().substring(member.getHeadpic().lastIndexOf("/") + 1, member.getHeadpic().length()));

            }
            Member mb = memberService.get(member.getId());
            member.setHeadpic(filename);
            memberService.update(mb);
//            memberService.update(new String[]{"headpic"}, new Object[]{member.getHeadpic()}, "where id='" + member.getId() + "'");
            accessRecordService.updateHeadpic(member.getId(), member.getHeadpic());

            //todo 待确认generate方法的作用
//            trajectoryService.generate(member, "更改了头像");

        }
        return "1";
    }


    @RequestMapping(value = "/updateNickName", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void updateNickName(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String u = request.getParameter("u");
        String nickName = request.getParameter("nickName");
        if (StringUtils.isEmpty(u) || StringUtils.isEmpty(nickName)) {
            ResponseUtils.json(response, 500, "信息要填写完整", null);
            return;
        } else {
            nickName = nickName.trim();
        }
        Member member = memberService.get(u);
//        MemberBean member = storeComponent.getMemberBeanFromMapDB(u);
        if (null == member) {
            ResponseUtils.json(response, 500, "用户不存在", null);
            return;
        }
        if (StringUtils.isEmpty(member.getNickName())) {
            member.setNickName(nickName);
        } else {
            if (member.getNickName().equals(nickName)) {
                ResponseUtils.json(response, 500, "昵称相同，无需修改", null);
                return;
            } else {
                Long l = memberService.count(new String[]{"nickName", "id"}, new Object[]{nickName, BaseDAO.NOTEQUALS + u});
                if (null != l && l > 0) {
                    ResponseUtils.json(response, 500, "此昵称已存在", null);
                    return;
                }
                member.setNickName(nickName);
            }
        }
        memberService.update(member);
//        memberService.update(new String[]{"nickName"}, new Object[]{member.getNickName()}, "where id='" + member.getId() + "'");
        accessRecordService.updateByEid(member.getId(), new String[]{"title"}, new String[]{member.getNickName()});

//		trajectoryService.generate(member, "更改了昵称");
        ResponseUtils.json(response, 200, member.getNickName(), null);
    }


    @RequestMapping(value = "/updateSex", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void updateSex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String u = request.getParameter("u");
        String sex = request.getParameter("sex");
        if (StringUtils.isEmpty(u) || StringUtils.isEmpty(sex)) {
            ResponseUtils.json(response, 500, "信息要填写完整", null);
            return;
        }
        Member member = memberService.get(u);
//        MemberBean member = storeComponent.getMemberBeanFromMapDB(u);
        if (null == member) {
            ResponseUtils.json(response, 500, "用户不存在", null);
            return;
        }
        member.setSex(sex);
        memberService.update(member);
//        memberService.update(new String[]{"sex"}, new Object[]{member.getSex()}, "where id='" + member.getId() + "'");
//		trajectoryService.generate(member, "更改了昵称");
        ResponseUtils.json(response, 200, member.getSex(), null);
    }


    @ResponseBody
    @RequestMapping(value = "/updatePwd", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void updatePwd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String u = request.getParameter("u");
        String oldPwd = request.getParameter("oldPwd");
        String newPwd = request.getParameter("newPwd");
        if (StringUtils.isEmpty(u) || StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd)) {
            ResponseUtils.json(response, 200, "参数缺失", null);
            return;
        }
        Member member = memberService.get(u);
        if (!new MD5().MD5Encode(oldPwd).equals(member.getPassword())) {
            ResponseUtils.json(response, 200, "原密码错误", null);
            return;
        } else {
            member.setPassword(new MD5().MD5Encode(newPwd));
        }
        memberService.update(member);
//        memberService.update(new String[]{"password"}, new Object[]{member.getPassword()}, "where id='" + member.getId() + "'");

        trajectoryService.generate(BeanUtils.memberToBeanSimple(member), "更改了密码");
        ResponseUtils.json(response, 200, "密码修改成功", null);
        return;
    }

    @ResponseBody
    @RequestMapping(value = "/updateTel", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void updateTel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String u = request.getParameter("u");
        String newTel = request.getParameter("newTel");
        if (StringUtils.isEmpty(u) || StringUtils.isEmpty(newTel)) {
            ResponseUtils.json(response, 200, "参数缺失", null);
            return;
        }
        Member member = memberService.get(u);
        if (member.getTelphone().equals(newTel)) {
            ResponseUtils.json(response, 200, "手机号码相同，无需修改", null);
            return;
        } else {
            member.setTelphone(newTel);
        }
        memberService.update(member);
//        memberService.update(new String[]{"telphone"}, new Object[]{member.getTelphone()}, "where id='" + member.getId() + "'");
        trajectoryService.generate(BeanUtils.memberToBeanSimple(member), "更改了手机号");
        ResponseUtils.json(response, 200, member.getTelphone(), null);
    }


    @ResponseBody
    @RequestMapping(value = "/loadById/v1", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void loadById(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id)) {
            ResponseUtils.json(response, 500, null, null);
            return;
        }
        Member member = memberService.get(id);
        if (null == member) return;
        MemberBean mb = new MemberBean();
        mb.setParent_uuid(member.getParent());
        mb.setUrl("http://" + configService.get().getWebsiteUrl() + "/user/json/i?u=" + member.getMemberId());
        mb.setMember_type(member.getMemberType());
        mb.setId(member.getId());
        if (StringUtils.isEmpty(member.getHeadpic())) {
            mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
        } else {
            mb.setHeadpic(member.getHeadpic());
        }
        if (storeComponent.isMemberOnline(id)) {
            mb.setOnline(1);
        }
        mb.setPreTxImg(member.getPreTxImg());
        mb.setPreTxPay(member.getPreTxPay());
        mb.setLastLoginDate(member.getLastLoginDate());
        mb.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
        mb.setLosePriceSum(member.getLosePriceSum());
        mb.setMemberId(member.getMemberId());
        mb.setMoney(member.getMoney());
        mb.setUsername(member.getUsername());
        mb.setNickName(member.getNickName());
        mb.setOpenRedCount(member.getOpenRedCount());
        mb.setOpenRedPriceSum(member.getOpenRedPriceSum());
        mb.setParent(member.getParent());
        mb.setRechargePriceSum(member.getRechargePriceSum());
        mb.setRegistIp(member.getRegistIp());
        mb.setSendRedCount(member.getSendRedCount());
        mb.setSendRedPriceSum(member.getSendRedPriceSum());
        mb.setStatus(member.getStatus());
        mb.setTelphone(member.getTelphone());
        mb.setTxPriceSum(member.getTxPriceSum());
        mb.setTxMoneyIng(member.getTxMoneyIng());
        mb.setWinPriceSum(member.getWinPriceSum());
        mb.setTichenPriceSum(member.getTichenPriceSum());
        mb.setQrCodeImg(member.getQrCodeImg());
        ResponseUtils.json(response, 200, mb, null);
    }


    @AuthPassport
    @ResponseBody
    @RequestMapping(value = "/load/v1", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void load_v1(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        if (StringUtils.isEmpty(MEMBERID)) {
            ResponseUtils.json(response, 500, null, null);
            return;
        }
        Member member = memberService.get(MEMBERID);
        if (null == member) return;
        MemberBean mb = new MemberBean();
        mb.setParent_uuid(member.getParent());
        mb.setUrl("http://" + configService.get().getWebsiteUrl() + "/user/json/i?u=" + member.getMemberId());
        mb.setMember_type(member.getMemberType());
        mb.setId(member.getId());
//        log.info("*****************************:" + member.getHeadpic() + ":++++++++++++++++++");
        if (StringUtils.isEmpty(member.getHeadpic())) {
            mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
        } else {
            mb.setHeadpic(member.getHeadpic());
        }
        mb.setSex(member.getSex());
        mb.setPreTxImg(member.getPreTxImg());
        mb.setPreTxPay(member.getPreTxPay());
        mb.setLastLoginDate(member.getLastLoginDate());
        mb.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
        mb.setLosePriceSum(member.getLosePriceSum());
        mb.setMemberId(member.getMemberId());
        mb.setMoney(member.getMoney());
        mb.setUsername(member.getUsername());
        mb.setNickName(member.getNickName());
        mb.setOpenRedCount(member.getOpenRedCount());
        mb.setOpenRedPriceSum(member.getOpenRedPriceSum());
        mb.setParent(member.getParent());
        mb.setRechargePriceSum(member.getRechargePriceSum());
        mb.setRegistIp(member.getRegistIp());
        mb.setSendRedCount(member.getSendRedCount());
        mb.setSendRedPriceSum(member.getSendRedPriceSum());
        mb.setStatus(member.getStatus());
        mb.setTelphone(member.getTelphone());
        mb.setTxPriceSum(member.getTxPriceSum());
        mb.setTxMoneyIng(member.getTxMoneyIng());
        mb.setWinPriceSum(member.getWinPriceSum());
        mb.setTichenPriceSum(member.getTichenPriceSum());
        mb.setQrCodeImg(member.getQrCodeImg());

        String[] ps = new String[]{"friendid", "status"};
        Object[] vs = new Object[]{member.getId(), FriendsAdd.Status.wait};
        Long l = memberService.friendsAddCount(ps, vs);
        mb.setUnDoFriendAddCount(l);


        ps = new String[]{"room_owner_id", "status"};
        vs = new Object[]{member.getId(), RoomAdd.Status.wait};
        l = roomService.roomAddCount(ps, vs);
        mb.setUnDoRoomAddCount(l);


        storeComponent.updateMemberBean(mb);

        ResponseUtils.json(response, 200, mb, null);
    }


    @ResponseBody
    @RequestMapping(value = "/load", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public String load(HttpServletRequest request) throws Exception {
        String u = request.getParameter("u");
        if (StringUtils.isEmpty(u)) return "";
        Member member = memberService.get(u);
        if (null == member) return "";
        MemberBean mb = new MemberBean();
        mb.setParent_uuid(member.getParent());
        mb.setUrl("http://" + configService.get().getWebsiteUrl() + "/user/json/i?u=" + member.getMemberId());
        mb.setMember_type(member.getMemberType());
        mb.setId(member.getId());
        if (StringUtils.isEmpty(member.getHeadpic())) {
            mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
        } else {
            mb.setHeadpic(member.getHeadpic());
        }
        mb.setPreTxImg(member.getPreTxImg());
        mb.setPreTxPay(member.getPreTxPay());
        mb.setLastLoginDate(member.getLastLoginDate());
        mb.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
        mb.setLosePriceSum(member.getLosePriceSum());
        mb.setMemberId(member.getMemberId());
        mb.setMoney(member.getMoney());
        mb.setUsername(member.getUsername());
        mb.setNickName(member.getNickName());
        mb.setOpenRedCount(member.getOpenRedCount());
        mb.setOpenRedPriceSum(member.getOpenRedPriceSum());
        mb.setParent(member.getParent());
        mb.setRechargePriceSum(member.getRechargePriceSum());
        mb.setRegistIp(member.getRegistIp());
        mb.setSendRedCount(member.getSendRedCount());
        mb.setSendRedPriceSum(member.getSendRedPriceSum());
        mb.setStatus(member.getStatus());
        mb.setTelphone(member.getTelphone());
        mb.setTxPriceSum(member.getTxPriceSum());
        mb.setTxMoneyIng(member.getTxMoneyIng());
        mb.setWinPriceSum(member.getWinPriceSum());
        mb.setTichenPriceSum(member.getTichenPriceSum());
        if (StringUtils.isEmpty(member.getQrCodeImg())) {
            TwoDimensionCode handler = new TwoDimensionCode();
            String fn = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
            handler.createQRCode("http://www.baidu.com", request.getRealPath("/img_sys/upload/member") + "/" + fn, "");
            member.setQrCodeImg("/img_sys/upload/member/" + fn);
//                memberService.update(member);
//            memberService.update(new String[]{"qrCodeImg"}, new Object[]{member.getQrCodeImg()}, "where id='" + member.getId() + "'");
        } else {
            File file = new File(request.getRealPath(member.getQrCodeImg()));
            if (!file.exists()) {
                TwoDimensionCode handler = new TwoDimensionCode();
                String fn = member.getNickName() + UUID.randomUUID().toString().replaceAll("-", "") + ".png";
                handler.createQRCode("http://www.baidu.com", request.getRealPath("/img_sys/upload/member") + "/" + fn, "");
                member.setQrCodeImg("/img_sys/upload/member/" + fn);
//                memberService.update(member);
//                memberService.update(new String[]{"qrCodeImg"}, new Object[]{member.getQrCodeImg()}, "where id='" + member.getId() + "'");
            }
        }
        mb.setQrCodeImg(member.getQrCodeImg());
        return JsonUtil.getJSONString(mb);
    }


    @RequestMapping(value = "/loginV2", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void loginV2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String addressIp = RabbitmqConfig.getLocalIP();
        String port = environment.getProperty("server.port");
        log.info("account:" + account + ";password:" + password);
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            ResponseUtils.json(response, 500, "账号或密码错误", null);
            return;
        }
        List<Member> list = memberService.getList(new String[]{"nickName", "password"}, new Object[]{account, MD5.MD5Encode(password)});

        if (null == list || list.isEmpty()) {
            list = memberService.getList(new String[]{"username", "password"}, new Object[]{account, MD5.MD5Encode(password)});
        }
//
//        if (null == list || list.isEmpty()) {
//            list = memberService.getList(new String[]{"telphone", "password"}, new Object[]{account, MD5.MD5Encode(password)});
//        }

        if (null == list || list.isEmpty()) {
            log.error("用户登陆失败,account:{};password:{}", account, password);
            ResponseUtils.json(response, 500, "手机/帐号或密码错误", null);
        } else {
            Member member = list.get(0);
            if (null != member.getStatus() && member.getStatus() == 1) {
                ResponseUtils.json(response, 500, "帐号禁用", null);
                return;
            }

            if (member.getMemberType() == MEMBER_TYPE.ROBOT) {
                ResponseUtils.json(response, 500, "该账号类型禁止登录", null);
                return;
            }


            boolean isEnabledRequest = ipListService.enableRequest(member, request);

            if (!isEnabledRequest) {
                log.error("拉黑用户禁止访问,member:{}; ip:{}", JSONObject.toJSONString(member),WebUtilsWeb.getIpAddrErrorLog(request));
                response.setStatus(HttpStatus.SC_BAD_GATEWAY);
                ResponseUtils.json(response, 502, "网络异常,请联系管理员", null);
                return;
            }

            request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, member.getId());
            String userIp = WebUtilsWeb.getIpAddr(request);
            MemberBean mb = new MemberBean();
            mb.setParent_uuid(member.getParent());
            mb.setMember_type(member.getMemberType());
            mb.setId(member.getId());
            mb.setUrl("http://" + configService.get().getWebsiteUrl() + "/user/json/i?u=" + member.getMemberId());
            if (StringUtils.isEmpty(member.getHeadpic())) {
                mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
            } else {
                mb.setHeadpic(member.getHeadpic());
            }
            mb.setPreTxImg(member.getPreTxImg());
            mb.setSex(member.getSex());
            mb.setPreTxPay(member.getPreTxPay());
            mb.setLastLoginDate(member.getLastLoginDate());
            mb.setLastLoginIp(userIp);
            mb.setLosePriceSum(member.getLosePriceSum());
            mb.setMemberId(member.getMemberId());
            mb.setMoney(member.getMoney());
            mb.setUsername(member.getUsername());
            mb.setNickName(member.getNickName());
            mb.setOpenRedCount(member.getOpenRedCount());
            mb.setOpenRedPriceSum(member.getOpenRedPriceSum());
            mb.setParent(member.getParent());
            mb.setRechargePriceSum(member.getRechargePriceSum());
            mb.setRegistIp(member.getRegistIp());
            mb.setSendRedCount(member.getSendRedCount());
            mb.setSendRedPriceSum(member.getSendRedPriceSum());
            mb.setStatus(member.getStatus());
            mb.setTelphone(member.getTelphone());
            mb.setPreBank_addr(member.getPreBank_addr());
            mb.setPreBank_belonger(member.getPreBank_belonger());
            mb.setPreBank_code(member.getPreBank_code());
            mb.setPreBank_name(member.getPreBank_name());
            mb.setTxPriceSum(member.getTxPriceSum());
            mb.setTxMoneyIng(member.getTxMoneyIng());
            mb.setWinPriceSum(member.getWinPriceSum());
            mb.setTichenPriceSum(member.getTichenPriceSum());

            member.setLastLoginIp(userIp);
            member.setLastLoginDate(new Date());
            memberService.update(member);

            mb.setQrCodeImg(member.getQrCodeImg());


            String[] ps = new String[]{"friendid", "status"};
            Object[] vs = new Object[]{member.getId(), FriendsAdd.Status.wait};
            Long l = memberService.friendsAddCount(ps, vs);
            mb.setUnDoFriendAddCount(l);


            ps = new String[]{"room_owner_id", "status"};
            vs = new Object[]{member.getId(), RoomAdd.Status.wait};
            l = roomService.roomAddCount(ps, vs);
            mb.setUnDoRoomAddCount(l);


            MemberLoginLog loginLog = new MemberLoginLog();
            loginLog.setCreateDate(new Date());
            loginLog.setIp(userIp);
            loginLog.setIpAddr(WebUtilsWeb.getIpAddress(userIp));
            loginLog.setMid(member.getMemberId());
            loginLog.setMnickName(member.getNickName());
            loginLog.setMtel(member.getTelphone());
            memberService.saveLoginLog(loginLog);


            chatStoreComponent.putMemberBean(member.getId(), BeanUtils.memberToBeanSimple(member));

            trajectoryService.generate(mb, "登陆了游戏(H5)");
            /**写入redis*/
            redisService.hPut(SessionStore.REDIS_WSS_KEY, member.getId(), addressIp + port);
            log.info("------登录结束");
            Object independenceRoomUUID = request.getSession().getAttribute("IndependenceRoomUUID");
            if (null == independenceRoomUUID || StringUtils.isEmpty(independenceRoomUUID.toString())) {
                ResponseUtils.json(response, 200, mb, null);
            } else {
//                RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(independenceRoomUUID.toString());
                RoomBean rb = chatStoreComponent.getRoomBeanMap(independenceRoomUUID.toString());
                if (rb.getIndependence() == 1) {
                    ResponseUtils.json(response, 200, mb, "/#/group/chat/" + independenceRoomUUID);
                } else {
                    ResponseUtils.json(response, 200, mb, null);
                }
            }
        }

    }

    @RequestMapping(value = "/loginV3", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void loginV3(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(account)) {
            ResponseUtils.json(response, 500, "用户数据错误", null);
            return;
        }


        List<Member> list = memberService.getList(new String[]{"id"}, new Object[]{account});
        ;

        if (null == list || list.isEmpty()) {
            list = memberService.getList(new String[]{"username", "password"}, new Object[]{account, new MD5().MD5Encode(password)});
        }

        if (null == list || list.isEmpty()) {
            list = memberService.getList(new String[]{"telphone", "password"}, new Object[]{account, new MD5().MD5Encode(password)});
        }

        if (null == list || list.isEmpty()) {
            ResponseUtils.json(response, 500, "手机/帐号或密码错误", null);
        } else {
            Member member = list.get(0);
            if (null != member.getStatus() && member.getStatus().intValue() == 1) {
                ResponseUtils.json(response, 500, "帐号禁用", null);
                return;
            }

            if (member.getMemberType() == MEMBER_TYPE.ROBOT) {
                ResponseUtils.json(response, 500, "该账号类型禁止登录", null);
                return;
            }

            request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, member.getId());
            MemberBean mb = new MemberBean();
            mb.setParent_uuid(member.getParent());
            mb.setMember_type(member.getMemberType());
            mb.setId(member.getId());
            mb.setUrl("http://" + configService.get().getWebsiteUrl() + "/user/json/i?u=" + member.getMemberId());
            if (StringUtils.isEmpty(member.getHeadpic())) {
                mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
            } else {
                mb.setHeadpic(member.getHeadpic());
            }
            mb.setPreTxImg(member.getPreTxImg());
            mb.setSex(member.getSex());
            mb.setPreTxPay(member.getPreTxPay());
            mb.setLastLoginDate(member.getLastLoginDate());
            mb.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
            mb.setLosePriceSum(member.getLosePriceSum());
            mb.setMemberId(member.getMemberId());
            mb.setMoney(member.getMoney());
            mb.setUsername(member.getUsername());
            mb.setNickName(member.getNickName());
            mb.setOpenRedCount(member.getOpenRedCount());
            mb.setOpenRedPriceSum(member.getOpenRedPriceSum());
            mb.setParent(member.getParent());
            mb.setRechargePriceSum(member.getRechargePriceSum());
            mb.setRegistIp(member.getRegistIp());
            mb.setSendRedCount(member.getSendRedCount());
            mb.setSendRedPriceSum(member.getSendRedPriceSum());
            mb.setStatus(member.getStatus());
            mb.setTelphone(member.getTelphone());
            mb.setPreBank_addr(member.getPreBank_addr());
            mb.setPreBank_belonger(member.getPreBank_belonger());
            mb.setPreBank_code(member.getPreBank_code());
            mb.setPreBank_name(member.getPreBank_name());
            mb.setTxPriceSum(member.getTxPriceSum());
            mb.setTxMoneyIng(member.getTxMoneyIng());
            mb.setWinPriceSum(member.getWinPriceSum());
            mb.setTichenPriceSum(member.getTichenPriceSum());
            if (StringUtils.isEmpty(member.getQrCodeImg())) {
                TwoDimensionCode handler = new TwoDimensionCode();
                String fn = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
                String P = "/img_sys/upload/member";
                String ossPathAndName = P + "/" + fn;
                member.setQrCodeImg("/img_sys/upload/member/" + fn);
//                handler.createQRCode("http://www.baidu.com", request.getRealPath(ossPathAndName) + "/" + fn, "");

                handler.createQRCodeV2("http://www.baidu.com", request.getRealPath("/img_sys/upload/member") + "/" + fn, "", ossUtil);
                member.setQrCodeImg(ossPathAndName);
//                memberService.update(new String[]{"qrCodeImg"}, new Object[]{ossPathAndName}, "where id='" + member.getId() + "'");
            } else {
                File file = new File(request.getRealPath(member.getQrCodeImg()));
                if (!file.exists()) {
                    TwoDimensionCode handler = new TwoDimensionCode();
                    String fn = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
                    String P = "/img_sys/upload/member";
                    String ossPathAndName = P + "/" + fn;
                    member.setQrCodeImg("/img_sys/upload/member/" + fn);
//                handler.createQRCode("http://www.baidu.com", request.getRealPath(ossPathAndName) + "/" + fn, "");

                    handler.createQRCodeV2("http://www.baidu.com", request.getRealPath("/img_sys/upload/member") + "/" + fn, "", ossUtil);
                    member.setQrCodeImg(ossPathAndName);
//                    memberService.update(new String[]{"qrCodeImg"}, new Object[]{ossPathAndName}, "where id='" + member.getId() + "'");
//                    memberService.update(new String[]{"qrCodeImg"}, new Object[]{member.getQrCodeImg()}, "where id='" + member.getId() + "'");
                }
            }

//            memberService.update(new String[]{"lastLoginIp", "lastLoginDate"}, new Object[]{WebUtilsWeb.getIpAddr(request), new Date()}, "where id='" + member.getId() + "'");

//                log.info("+++++++++++++++++++++++++++mb.getHeadpic(imageOssPath):3:" + member.getHeadpic());
            member.setLastLoginDate(new Date());
            member.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
            memberService.update(member);
            accessRecordService.updateHeadpic(mb.getId(), mb.getHeadpic());

            mb.setQrCodeImg(member.getQrCodeImg());


            String[] ps = new String[]{"friendid", "status"};
            Object[] vs = new Object[]{member.getId(), FriendsAdd.Status.wait};
            Long l = memberService.friendsAddCount(ps, vs);
            mb.setUnDoFriendAddCount(l);


            ps = new String[]{"room_owner_id", "status"};
            vs = new Object[]{member.getId(), RoomAdd.Status.wait};
            l = roomService.roomAddCount(ps, vs);
            mb.setUnDoRoomAddCount(l);


            MemberLoginLog log = new MemberLoginLog();
            log.setCreateDate(new Date());
            log.setIpAddr(WebUtilsWeb.getIpAddress(WebUtilsWeb.getIpAddr(request)));
            log.setIp(WebUtilsWeb.getIpAddr(request));
            log.setIpAddr(WebUtilsWeb.getIpAddress(WebUtilsWeb.getIpAddr(request)));
            log.setMid(member.getMemberId());
            log.setMnickName(member.getNickName());
            log.setMtel(member.getTelphone());
            memberService.saveLoginLog(log);


            chatStoreComponent.putMemberBean(member.getId(), BeanUtils.memberToBeanSimple(member));

            trajectoryService.generate(mb, "登陆了游戏(H5)");
            Object independenceRoomUUID = request.getSession().getAttribute("IndependenceRoomUUID");
            if (null == independenceRoomUUID || StringUtils.isEmpty(independenceRoomUUID.toString())) {
                ResponseUtils.json(response, 200, mb, null);
            } else {
//                RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(independenceRoomUUID.toString());
                RoomBean rb = chatStoreComponent.getRoomBeanMap(independenceRoomUUID.toString());
                if (rb.getIndependence() == 1) {
                    ResponseUtils.json(response, 200, mb, "/#/group/chat/" + independenceRoomUUID);
                } else {
                    ResponseUtils.json(response, 200, mb, null);
                }

            }
        }
    }

    @RequestMapping(value = "/registerV2", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void registerV2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebConfig wc = configService.get();

        Random random = new Random();
        String account = String.valueOf(1000000 + random.nextInt(999999));
        if (!StringUtils.isEmpty(wc.getForbidRegister4ip()) &&
                wc.getForbidRegister4ip().indexOf(WebUtilsWeb.getIpAddr(request)) >= 0) {
            ResponseUtils.json(response, 500, "注册失败", null);
            return;
        }


        boolean isIpBlacked = ipListService.isInBlackIps(request);
        if (isIpBlacked) {
            response.setStatus(HttpStatus.SC_BAD_GATEWAY);
            ResponseUtils.json(response, 502, "网络异常,请联系管理员", null);
            return;
        }


        EmployeeDefaultMessage e = null;
        //员工邀请码
        if (wc.getUseInviteCode() == 1) {
            String inviteCode = request.getParameter("inviteCode");
            if (StringUtils.isEmpty(inviteCode)) {
                ResponseUtils.json(response, 500, "邀请码不正确", null);
                return;
            }
            e = employeeDefaultMessageService.get("invite_code", inviteCode);

            if (null == e) {
                ResponseUtils.json(response, 500, "邀请码不正确", null);
                return;
            }
        }else {
            ResponseUtils.json(response, 500, "没有邀请码不能登录", null);
            return;
        }


        String nickname = request.getParameter("nickname");
        String password = request.getParameter("password");


        Long ll = memberService.count(new String[]{"nickName"}, new Object[]{nickname});
        if (null != ll && ll > 0) {
            ResponseUtils.json(response, 500, "昵称已存在", null);
            return;
        }

        Member member = new Member();

        member.setMemberType(MEMBER_TYPE.USER);
        member.setPassword(MD5.MD5Encode(password));
        member.setRegistIp(WebUtilsWeb.getIpAddr(request));
        if (!StringUtils.isEmpty(nickname)) {
            member.setNickName(nickname);
        } else {
            member.setNickName(nickname);
        }
        String redisKey = "memberId";
        Long number = redisService.incrBy(redisKey, 1L);
        member.setMemberId(number.toString());
        member.setCreateDate(new Date());
        member.setStatus(0);
        member.setLastLoginDate(new Date());
        member.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
        member.setUsername("member" + number);
        member.setTelphone(MemberConstant.default_telphone);
        memberService.save(member);


        request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, member.getId());

        MemberBean mb = new MemberBean();
        mb.setParent_uuid(member.getParent());
        mb.setMember_type(member.getMemberType());
        mb.setId(member.getId());
        if (StringUtils.isEmpty(member.getHeadpic())) {
            mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
        } else {
            mb.setHeadpic(member.getHeadpic());
        }
        mb.setPreTxImg(member.getPreTxImg());
        mb.setPreTxPay(member.getPreTxPay());

        mb.setLastLoginDate(member.getLastLoginDate());
        mb.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
        mb.setLosePriceSum(member.getLosePriceSum());
        mb.setMemberId(member.getMemberId());
        mb.setMoney(member.getMoney());
        mb.setUsername(member.getUsername());
        mb.setNickName(member.getNickName());
        mb.setOpenRedCount(member.getOpenRedCount());
        mb.setOpenRedPriceSum(member.getOpenRedPriceSum());
        mb.setParent(member.getParent());
        mb.setRechargePriceSum(member.getRechargePriceSum());
        mb.setRegistIp(member.getRegistIp());
        mb.setSendRedCount(member.getSendRedCount());
        mb.setSendRedPriceSum(member.getSendRedPriceSum());
        mb.setStatus(member.getStatus());
        mb.setTelphone(member.getTelphone());
        mb.setTxPriceSum(member.getTxPriceSum());
        mb.setTxMoneyIng(member.getTxMoneyIng());
        mb.setWinPriceSum(member.getWinPriceSum());
        mb.setTichenPriceSum(member.getTichenPriceSum());
        mb.setQrCodeImg(member.getQrCodeImg());
        mb.setPreBank_addr(member.getPreBank_addr());
        mb.setPreBank_belonger(member.getPreBank_belonger());
        mb.setPreBank_code(member.getPreBank_code());
        mb.setPreBank_name(member.getPreBank_name());

        String[] ps = new String[]{"friendid", "status"};
        Object[] vs = new Object[]{member.getId(), FriendsAdd.Status.wait};
        ll = memberService.friendsAddCount(ps, vs);
        mb.setUnDoFriendAddCount(ll);

        ps = new String[]{"room_owner_id", "status"};
        vs = new Object[]{member.getId(), RoomAdd.Status.wait};
        ll = roomService.roomAddCount(ps, vs);
        mb.setUnDoRoomAddCount(ll);

        MemberLoginLog log = new MemberLoginLog();
        log.setCreateDate(new Date());
        log.setIpAddr(WebUtilsWeb.getIpAddress(WebUtilsWeb.getIpAddr(request)));
        log.setIp(WebUtilsWeb.getIpAddr(request));
        log.setMid(member.getMemberId());
        log.setMnickName(member.getNickName());
        log.setMtel(member.getTelphone());
        memberService.saveLoginLog(log);

        chatStoreComponent.putMemberBean(member.getId(), BeanUtils.memberToBeanSimple(member));

        WebUtils.setSessionAttribute(request, "TelCheckCode", null);
        WebUtils.setSessionAttribute(request, "sendSms_mobile", null);


        //员工邀请码
        if (wc.getUseInviteCode() == 1) {
            Member memberEmployee = memberService.get("memberId", e.getMember_id());
            MemberBean mb1 = storeComponent.getMemberBeanFromMapDB(memberEmployee.getId());
            Friends f = new Friends();
            f.setCreateDate(new Date());
            f.setFriendid(mb1.getId());
            f.setMid(member.getId());
            memberService.saveFriends(f);
            f = new Friends();
            f.setCreateDate(new Date());
            f.setFriendid(member.getId());
            f.setMid(mb1.getId());
            memberService.saveFriends(f);
            ChatTxtBean bean = new ChatTxtBean();
            bean.setFromUid(member.getId());
            bean.setToUid(mb1.getId());
            bean.setTxt("我们已经成为好友了，现在可以开始聊天了！");
            userChatCmd.sendTXT(bean);

        }


        MemberBean m = storeComponent.getMemberBeanFromMapDB("-1");//官方团队
        //添加官方团队好友
        Friends f = new Friends();
        f.setCreateDate(new Date());
        f.setFriendid(m.getId());
        f.setMid(member.getId());
        memberService.saveFriends(f);

        if (!StringUtils.isEmpty(wc.getNewRegAddFriends())) {
            String[] rs = wc.getNewRegAddFriends().split(",");
            for (String r : rs) {
                if (StringUtils.isEmpty(r)) continue;
                Member fri = memberService.get("memberId", r);
                if (null == fri) continue;
                f = new Friends();
                f.setCreateDate(new Date());
                f.setFriendid(fri.getId());
                f.setMid(member.getId());
                memberService.saveFriends(f);
                f = new Friends();
                f.setCreateDate(new Date());
                f.setFriendid(member.getId());
                f.setMid(fri.getId());
                memberService.saveFriends(f);

                if (!StringUtils.isEmpty(wc.getNewRegAddFriends_welcomeWord())) {
                    //
                    ChatTxtBean bean = new ChatTxtBean();
                    bean.setFromUid(fri.getId());
                    bean.setToUid(member.getId());
                    //bean.setTxt("您被【"+member.getNickName()+"】邀请加入【"+room.getName()+"】群组");
                    bean.setTxt(wc.getNewRegAddFriends_welcomeWord());
                    userChatCmd.sendTXT(bean);
                    //
                }
            }
        }


        if (!StringUtils.isEmpty(wc.getWelcomeStr())) {
            ChatTxtBean bean = new ChatTxtBean();
            bean.setFromUid("-1");
            bean.setToUid(member.getId());
            bean.setTxt(wc.getWelcomeStr());
            userChatCmd.sendTXT(bean);
        }


        String inviteCode = request.getParameter("inviteCode");
        if (!StringUtils.isEmpty(inviteCode)) {
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(5000);
                    afterRegisterService.afterRegister(member, inviteCode);
                } catch (Exception e1) {

                }
            });
        }

        ResponseUtils.json(response, 200, mb, null);
    }

    @RequestMapping("/{inviteCode}")
    public String registerRedirect(@PathVariable(value = "inviteCode") String inviteCode, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebConfig wc = configService.get();
        if (StringUtils.isEmpty(inviteCode)) {
            ResponseUtils.json(response, 500, "邀请码不正确", null);
            return "redirect:".concat("https://baidu.com");
        }

        String redisKey = "visitorUser";
        Long number = redisService.incrBy(redisKey, 1L);

        String nickname = "咨询用户" + number;
        String password = String.valueOf(new Random(99999));


        Long ll = memberService.count(new String[]{"nickName"}, new Object[]{nickname});
        while (null != ll && ll > 0) {
            number = redisService.incrBy(redisKey, 1L);
            nickname = "咨询用户" + number;
            ll = memberService.count(new String[]{"nickName"}, new Object[]{nickname});
        }

        Member member = new Member();

        member.setMemberType(MEMBER_TYPE.USER);


        member.setPassword(MD5.MD5Encode(password));
        member.setRegistIp(WebUtilsWeb.getIpAddr(request));
        if (!StringUtils.isEmpty(nickname)) {
            member.setNickName(nickname);
        } else {
            member.setNickName(nickname);
        }
        String redisKeyMember = "memberId";
        Long memberNumber = redisService.incrBy(redisKeyMember, 1L);
        member.setMemberId(memberNumber.toString());
        member.setCreateDate(new Date());
        member.setStatus(0);
        member.setLastLoginDate(new Date());
        member.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
        member.setTelphone(MemberConstant.default_telphone);
        member.setUsername(nickname);
        memberService.save(member);


        request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, member.getId());

        MemberBean mb = new MemberBean();
        mb.setParent_uuid(member.getParent());
        mb.setMember_type(member.getMemberType());
        mb.setId(member.getId());
        if (StringUtils.isEmpty(member.getHeadpic())) {
            mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
        } else {
            mb.setHeadpic(member.getHeadpic());
        }
        mb.setPreTxImg(member.getPreTxImg());
        mb.setPreTxPay(member.getPreTxPay());

        mb.setLastLoginDate(member.getLastLoginDate());
        mb.setLastLoginIp(WebUtilsWeb.getIpAddr(request));
        mb.setLosePriceSum(member.getLosePriceSum());
        mb.setMemberId(member.getMemberId());
        mb.setMoney(member.getMoney());
        mb.setUsername(member.getUsername());
        mb.setNickName(member.getNickName());
        mb.setOpenRedCount(member.getOpenRedCount());
        mb.setOpenRedPriceSum(member.getOpenRedPriceSum());
        mb.setParent(member.getParent());
        mb.setRechargePriceSum(member.getRechargePriceSum());
        mb.setRegistIp(member.getRegistIp());
        mb.setSendRedCount(member.getSendRedCount());
        mb.setSendRedPriceSum(member.getSendRedPriceSum());
        mb.setStatus(member.getStatus());
        mb.setTelphone(member.getTelphone());
        mb.setTxPriceSum(member.getTxPriceSum());
        mb.setTxMoneyIng(member.getTxMoneyIng());
        mb.setWinPriceSum(member.getWinPriceSum());
        mb.setTichenPriceSum(member.getTichenPriceSum());
        mb.setQrCodeImg(member.getQrCodeImg());
        mb.setPreBank_addr(member.getPreBank_addr());
        mb.setPreBank_belonger(member.getPreBank_belonger());
        mb.setPreBank_code(member.getPreBank_code());
        mb.setPreBank_name(member.getPreBank_name());

        String[] ps = new String[]{"friendid", "status"};
        Object[] vs = new Object[]{member.getId(), FriendsAdd.Status.wait};
        ll = memberService.friendsAddCount(ps, vs);
        mb.setUnDoFriendAddCount(ll);

        ps = new String[]{"room_owner_id", "status"};
        vs = new Object[]{member.getId(), RoomAdd.Status.wait};
        ll = roomService.roomAddCount(ps, vs);
        mb.setUnDoRoomAddCount(ll);

        MemberLoginLog log = new MemberLoginLog();
        log.setCreateDate(new Date());
        log.setIpAddr(WebUtilsWeb.getIpAddress(WebUtilsWeb.getIpAddr(request)));
        log.setIp(WebUtilsWeb.getIpAddr(request));
        log.setMid(member.getMemberId());
        log.setMnickName(member.getNickName());
        log.setMtel(member.getTelphone());
        memberService.saveLoginLog(log);

        chatStoreComponent.putMemberBean(member.getId(), BeanUtils.memberToBeanSimple(member));

        WebUtils.setSessionAttribute(request, "TelCheckCode", null);
        WebUtils.setSessionAttribute(request, "sendSms_mobile", null);


        //员工邀请码
        if (wc.getUseInviteCode() == 1) {


            EmployeeDefaultMessage e = employeeDefaultMessageService.get("invite_code", inviteCode);

            if (null == e) {
                ResponseUtils.json(response, 500, "邀请码不正确", null);
                return "redirect:".concat("https://baidu.com");
            }
            Member memberEmployee = memberService.get("memberId", e.getMember_id());

            MemberBean mb1 = storeComponent.getMemberBeanFromMapDB(memberEmployee.getId());


            Friends f = new Friends();
            f.setCreateDate(new Date());
            f.setFriendid(mb1.getId());
            f.setMid(member.getId());
            memberService.saveFriends(f);


            f = new Friends();
            f.setCreateDate(new Date());
            f.setFriendid(member.getId());
            f.setMid(mb1.getId());
            memberService.saveFriends(f);

            ChatTxtBean bean = new ChatTxtBean();
            bean.setFromUid(member.getId());
            bean.setToUid(mb1.getId());
            bean.setTxt("我们已经成为好友了，现在可以开始聊天了！");
            userChatCmd.sendTXT(bean);

        }


        MemberBean m = storeComponent.getMemberBeanFromMapDB("-1");//官方团队
        //添加官方团队好友
        Friends f = new Friends();
        f.setCreateDate(new Date());
        f.setFriendid(m.getId());
        f.setMid(member.getId());
        memberService.saveFriends(f);

        if (!StringUtils.isEmpty(wc.getNewRegAddFriends())) {
            String[] rs = wc.getNewRegAddFriends().split(",");
            for (String r : rs) {
                if (StringUtils.isEmpty(r)) continue;
                Member fri = memberService.get("memberId", r);
                if (null == fri) continue;
                f = new Friends();
                f.setCreateDate(new Date());
                f.setFriendid(fri.getId());
                f.setMid(member.getId());
                memberService.saveFriends(f);
                f = new Friends();
                f.setCreateDate(new Date());
                f.setFriendid(member.getId());
                f.setMid(fri.getId());
                memberService.saveFriends(f);

                if (!StringUtils.isEmpty(wc.getNewRegAddFriends_welcomeWord())) {
                    //
                    ChatTxtBean bean = new ChatTxtBean();
                    bean.setFromUid(fri.getId());
                    bean.setToUid(member.getId());
                    //bean.setTxt("您被【"+member.getNickName()+"】邀请加入【"+room.getName()+"】群组");
                    bean.setTxt(wc.getNewRegAddFriends_welcomeWord());
                    userChatCmd.sendTXT(bean);
                    //
                }


                ChatTxtBean bean = new ChatTxtBean();
                bean.setFromUid(member.getId());
                bean.setToUid(fri.getId());
                bean.setTxt("我们已经成为好友了，现在可以开始聊天了！");
                userChatCmd.sendTXT(bean);

            }
        }


        if (!StringUtils.isEmpty(wc.getWelcomeStr())) {
            ChatTxtBean bean = new ChatTxtBean();
            bean.setFromUid("-1");
            bean.setToUid(member.getId());
            bean.setTxt(wc.getWelcomeStr());
            userChatCmd.sendTXT(bean);
        }

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(5000);
                afterRegisterService.afterRegister(member, inviteCode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
//        return redirect(h5Model.getUrl() + "?userid=" + member.getMemberId());
        return "redirect:".concat(h5Model.getUrl() + "?userid=" + member.getId());
    }

    //    @AuthPassport
    @GetMapping("/userInfo")
    public void getUserInfo(@RequestParam(value = "toid") String toid, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = request.getHeader("x-access-uid");
        if (StringUtils.isEmpty(uid)) {
            ResponseUtils.json(response, 500, "当前登录用户异常", null);
            return;
        }
        Employee employee = employeeService.get("member_uuid", uid);
        if (null == employee) {
//            Member  member = memberService.get(uid);
//            String mid = member.getMemberId();
//            MemberLoginLog memberLoginLog = memberService.getIP("mid", mid);
//            Map<String, String> map = new HashMap<>();
//            map.put("ip", memberLoginLog.getIp());
//            map.put("ipAddr", memberLoginLog.getIpAddr());
            ResponseUtils.json(response, 200, "", null);
            return;
        }
        if (null == toid || toid.isEmpty()) {
            ResponseUtils.json(response, 200, "", null);
            return;
        }

        Member member = memberService.get(toid);
        String mid = member.getMemberId();
        MemberLoginLog memberLoginLog = memberService.getIP("mid", mid);

        if (null == memberLoginLog) {
            ResponseUtils.json(response, 200, "", null);
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("ip", memberLoginLog.getIp());
        map.put("ipAddr", memberLoginLog.getIpAddr());
        ResponseUtils.json(response, 200, map, null);
    }

}
