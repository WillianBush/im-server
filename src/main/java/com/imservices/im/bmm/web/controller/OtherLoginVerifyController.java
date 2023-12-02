package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.bean.MemberBean;
import com.imservices.im.bmm.bean.RoomBean;
import com.imservices.im.bmm.bean.store.ChatStore;
import com.imservices.im.bmm.bean.store.StoreComponent;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.*;
import com.imservices.im.bmm.entity.Member.MEMBER_TYPE;
import com.imservices.im.bmm.service.*;
import com.imservices.im.bmm.utils.JsonUtil;
import com.imservices.im.bmm.utils.MD5;
import com.imservices.im.bmm.utils.TwoDimensionCode;
import com.imservices.im.bmm.utils.olv.HttpClientConnectionManager;
import com.imservices.im.bmm.utils.olv.QQHttpClient;
import com.imservices.im.bmm.utils.olv.WeixinOauth2Token;
import com.imservices.im.bmm.utils.olv.WeixinOauth2Userinfo;
import com.imservices.im.bmm.utils.web.BeanUtils;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import com.alibaba.fastjson.JSONObject;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

@Controller("OtherLoginVerifyController")
@RequestMapping(value = "/olv")
@CrossOrigin
public class OtherLoginVerifyController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private WebConfigService configService;
    @Autowired
    private TrajectoryService trajectoryService;
    @Autowired
    private TempEtService tempEtService;
    @Autowired
    private RoomService roomService;

    @RequestMapping(value = "/goBack", method = {RequestMethod.GET})
    public String goBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = request.getParameter("url");
        String te = request.getParameter("te");//是否扫码登陆的
        String err = request.getParameter("err");
        String mid = request.getParameter("mid");
        if (!StringUtils.isEmpty(mid)) {
            request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, mid);
        }

        Object independenceRoomUUID = request.getSession().getAttribute("IndependenceRoomUUID");
        if (null == independenceRoomUUID || StringUtils.isEmpty(independenceRoomUUID.toString())) {
            request.setAttribute("url", StringUtils.isEmpty(url) ? "" : url);
        } else {
            RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(independenceRoomUUID.toString());
            if (rb.getIndependence() == 1) {
                request.setAttribute("url", "/#/group/chat/" + independenceRoomUUID);
            } else {
                request.setAttribute("url", StringUtils.isEmpty(url) ? "" : url);
            }

        }

        request.setAttribute("te", StringUtils.isEmpty(te) ? "" : te);
        request.setAttribute("err", StringUtils.isEmpty(err) ? "" : err);
        return "web/otherLoginBack";
    }


    @RequestMapping(value = "/tempEt4WxLoginDo", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void tempEt4WxLoginDo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tid = request.getParameter("tid");
        TempEt et = tempEtService.get(tid);
        if (StringUtils.isEmpty(et.getVal2())) {
            ResponseUtils.json(response, 404, "", null);
            return;
        }
        MemberBean bean = ChatStore.USER_BEAN_MAP.get(et.getVal2());
        request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, et.getVal2());

        Object independenceRoomUUID = request.getSession().getAttribute("IndependenceRoomUUID");
        if (null == independenceRoomUUID || StringUtils.isEmpty(independenceRoomUUID.toString())) {
            ResponseUtils.json(response, 200, bean, et.getVal3());
        } else {
            RoomBean rb = ChatStore.ROOMB_BEAN_MAP.get(independenceRoomUUID.toString());
            if (rb.getIndependence() == 1) {
                ResponseUtils.json(response, 200, bean, "/#/group/chat/" + independenceRoomUUID);
            } else {
                ResponseUtils.json(response, 200, bean, et.getVal3());
            }

        }
        //tempEtService.delete(et);
    }

    @RequestMapping(value = "/getTempEt4WxLogin", method = {RequestMethod.POST, RequestMethod.OPTIONS})
    public void getTempEt4WxLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebConfig wc = configService.get();
        TempEt o = new TempEt();
        Object $invitor = request.getSession().getAttribute("$invitor");
        if (null != $invitor && !StringUtils.isEmpty($invitor.toString())) {
            o.setVal4($invitor.toString());//上级memberID
        }
        tempEtService.save(o);
        o.setVal1("http://" + wc.getWx_domain() + "/olv/goWxLogin?tid=" + o.getId());
        tempEtService.update(o);
        ResponseUtils.json(response, 200, o, null);
    }

    @RequestMapping(value = "/goWxLogin", method = {RequestMethod.GET})
    public void goWxLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebConfig wc = configService.get();
        String state = UUID.randomUUID().toString();
        request.getSession().setAttribute("wx_state", state);
        //
        String tid = request.getParameter("tid");
        String pid = request.getParameter("pid");
        String callback = "";
        //


        Object invitor = request.getSession().getAttribute("$invitor");
        String par = "";
        if (null != invitor) {
            par = "&invitor=" + invitor;
        }

        if (!StringUtils.isEmpty(tid)) {
//	    	if (!StringUtils.isEmpty(pid)) {
//	    		callback = "http://"+wc.getWebsiteUrl() + "/olv/wxLoginBack?qcode=" + qcode+"&pid="+pid;
//	    	} else {
//	    		callback = "http://"+wc.getWebsiteUrl() + "/olv/wxLoginBack?qcode=" + qcode;
//	    	}
            callback = "http://" + wc.getWx_domain() + "/olv/wxLoginBack?tid=" + tid + "&fdomain=" + request.getServerName() + par;
        } else {
            //如果是WX内打开的。需要把当前域名 保存起来，方便回调的时候进行跳转
//	    	TempEt o = new TempEt();
//	 	    o.setVal1(request.getServerName());
//	 	    tempEtService.save(o);


            callback = "http://" + wc.getWx_domain() + "/olv/wxLoginBack?fdomain=" + request.getServerName() + par;
        }

        String domain = request.getServerName();
        Room room = roomService.get("domain", domain);
        if (null != room && room.getIndependence() == 1) {
            request.getSession().setAttribute("IndependenceRoomUUID", room.getId());//用于作为已进入独立房间的标记
            callback += "&IndependenceRoomUUID=" + room.getId();
        }

        //
        response.sendRedirect("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + wc.getWx_appid() + "&redirect_uri=" + URLEncoder.encode(callback) + "&response_type=code&scope=snsapi_userinfo&state=" + state + "#wechat_redirect");
    }

    @RequestMapping(value = "/wxLoginBack", method = {RequestMethod.GET})
    public void wxLoginBack(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            //
            WebConfig wc = configService.get();
            String code = request.getParameter("code");
            String state = request.getParameter("state");
            String tid = request.getParameter("tid");
            String temp_state = (String) request.getSession().getAttribute("wx_state");
            String fdomain = request.getParameter("fdomain");
            String invitor = request.getParameter("invitor");


            String IndependenceRoomUUID = request.getParameter("IndependenceRoomUUID");
            //
            if (!StringUtils.isEmpty(IndependenceRoomUUID)) {
                //
                request.getSession().setAttribute("IndependenceRoomUUID", IndependenceRoomUUID);//用于作为已进入独立房间的标记
            }


//	      //
//	      //
//	      //
//	      // 
//	      if ((StringUtils.isEmpty(temp_state)) || (StringUtils.isEmpty(state)) || (!state.equals(temp_state))) {
//	        return; 
//	      }
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient);

            HttpPost httpost = HttpClientConnectionManager.getPostMethod("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + wc.getWx_appid() + "&secret=" + wc.getWx_secret() + "&code=" + code + "&grant_type=authorization_code");
            HttpResponse response_http = httpclient.execute(httpost);
            String jsonStr = EntityUtils.toString(response_http.getEntity(), "UTF-8");
            //
            if (jsonStr.indexOf("FAIL") != -1) {
                return;
            }
            WeixinOauth2Token tk = (WeixinOauth2Token) JsonUtil.getDTO(jsonStr, WeixinOauth2Token.class);
            //

            Member member = memberService.get("wxOpenId", tk.getOpenid());
            if (null == member) {
                //
                httpost = HttpClientConnectionManager.getPostMethod("https://api.weixin.qq.com/sns/userinfo?access_token=" + tk.getAccess_token() + "&openid=" + tk.getOpenid() + "&lang=zh_CN");
                response_http = httpclient.execute(httpost);
                jsonStr = EntityUtils.toString(response_http.getEntity(), "UTF-8");
                //
                if (jsonStr.indexOf("FAIL") != -1) {
                    return;
                }
                WeixinOauth2Userinfo userinfo = (WeixinOauth2Userinfo) JsonUtil.getDTO(jsonStr, WeixinOauth2Userinfo.class);
                //
                //

                member = new Member();
                //注册初始金额
                if (null != wc.getUserRegisterGiveGold()) {
                    member.setMoney(wc.getUserRegisterGiveGold());
                }
                Object $invitor = request.getSession().getAttribute("$invitor");
                if (null == $invitor || StringUtils.isEmpty($invitor.toString())) {
                    if (!StringUtils.isEmpty(invitor)) {
                        $invitor = invitor;
                    }

                }
                //
                if (null != $invitor && !StringUtils.isEmpty($invitor.toString())) {
                    Member parent = memberService.get("memberId", $invitor.toString());
                    //
                    if (null != parent) {
                        member.setParent(parent.getId());
                        member.setParentmid(parent.getMemberId());
                        member.setParentPath(parent.getParentPath());
                        request.getSession().removeAttribute("$invitor");
                        Member parentMem = memberService.get(member.getParent());
                        parentMem.setOne_level_count(1);
                        memberService.update(parentMem);
//					memberService.update(new String[]{"one_level_count+"}, new Object[]{1}, "where id='"+parent.getId()+"'");
                    }
                }

                String username = "wx_" + (int) ((Math.random() * 9.0D + 1.0D) * 100000.0D);
                Long l = memberService.count(new String[]{"username"}, new Object[]{username});
                while (null != l && l > 0) {
                    username = "wx_" + (int) ((Math.random() * 9.0D + 1.0D) * 100000.0D);
                    l = memberService.count(new String[]{"username"}, new Object[]{username});
                }

                member.setMemberType(MEMBER_TYPE.USER);
                member.setUsername(username);
                member.setWxOpenId(tk.getOpenid());
                member.setTelphone("");
                member.setPassword(MD5.MD5Encode("123456"));
                member.setRegistIp(request.getRemoteAddr());
                member.setNickName(EmojiParser.removeAllEmojis(userinfo.getNickname()));
                member.setCreateDate(new Date());
                TwoDimensionCode handler = new TwoDimensionCode();
                String fn = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
                handler.createQRCode("http://www.baidu.com", request.getRealPath("/images/upload/member") + "/" + fn, "");
                member.setQrCodeImg("/images/upload/member/" + fn);
                member.setMemberId(memberService.generateMemberId() + "");
                member.setStatus(0);
                member.setLastLoginDate(new Date());
                member.setLastLoginIp(request.getRemoteAddr());
                member.setHeadpic(userinfo.getHeadimgurl());
                memberService.save(member);

                member.setParentPath(member.getParentPath() + member.getId() + ",");


                //TODO shenghong
//                tongjiService.update(new String[]{"todayRegisterCount+"}, new Object[]{1});

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
                mb.setUrl("http://" + configService.get().getWx_domain() + "/user/json/i?u=" + member.getMemberId());

                mb.setLastLoginDate(member.getLastLoginDate());
                mb.setLastLoginIp(member.getLastLoginIp());
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

                MemberLoginLog log = new MemberLoginLog();
                log.setCreateDate(new Date());
                log.setIp(request.getRemoteAddr());
                log.setMid(member.getMemberId());
                log.setMnickName(member.getNickName());
                log.setMtel(member.getTelphone());
                memberService.saveLoginLog(log);
                ChatStore.USER_BEAN_MAP.put(member.getId(), BeanUtils.memberToBeanSimple(member));
                if (!StringUtils.isEmpty(tid)) {
                    TempEt et = tempEtService.get(tid);
                    et.setVal2(member.getId());

                    if (!StringUtils.isEmpty(et.getVal4())) {
                        Member parent = memberService.get("memberId", et.getVal4());
                        if (null != parent) {
                            member.setParent(parent.getId());
                            mb.setParent(parent.getId());
                            member.setParentmid(parent.getMemberId());
                            member.setParentPath(parent.getParentPath());
                            request.getSession().removeAttribute("$invitor");
                            Member parentMem = memberService.get(member.getParent());
                            parentMem.setOne_level_count(1);
                            memberService.update(parentMem);
//						memberService.update(new String[]{"one_level_count+"}, new Object[]{1}, "where id='"+parent.getId()+"'");
                        }
                    }

                    if (!StringUtils.isEmpty(mb.getParent())) {
                        et.setVal3(wc.getDailiUrlRegisteredTo());
                    }

                    tempEtService.update(et);

                    //response.setCharacterEncoding("utf-8");
                    //response.setContentType("text/html;charset=utf-8");
                    //response.getWriter().print("<div style='color:#222;font-size:50px;text-align:center;width:100%;margin-top:100px'><img style='width:240px' src='/images/bb2.png'/><div style='margin-top:30px'>登录成功</div><div style='font-size:32px;color:#aaa;   width: 90%;margin: auto auto;margin-top: 30px;'>您已使用微信登录平台，返回APP即可进入</div></div>");
                    response.sendRedirect("http://" + fdomain + "/olv/goBack?te=1&mid=" + member.getId());
                    return;
                } else {
                    if (!StringUtils.isEmpty(mb.getParent())) {
                        //ResponseUtils.json(response, 200,mb ,wc.getDailiUrlRegisteredTo() );
                        response.sendRedirect("http://" + fdomain + "/olv/goBack?url=" + wc.getDailiUrlRegisteredTo() + "&mid=" + member.getId());
                    } else {
                        //response.sendRedirect("http://"+wc.getWebsiteUrl()+"/");
                        response.sendRedirect("http://" + fdomain + "/olv/goBack?mid=" + member.getId());
                    }
                }

                memberService.update(member);
                trajectoryService.generate(mb, "使用微信注册并登陆了游戏");
            } else {
                //
                if (null != member.getStatus() && member.getStatus().intValue() == 1) {
                    //ResponseUtils.json(response, 500, "帐号禁用", null);
                    //response.setCharacterEncoding("utf-8");
                    //response.setContentType("text/html;charset=utf-8");
                    //response.getWriter().print("<div style='color:#222;font-size:50px;text-align:center;width:100%;margin-top:100px'><img style='width:240px' src='/images/as4.png'/><div style='margin-top:30px'>登录失败</div><div style='font-size:32px;color:#aaa;   width: 90%;margin: auto auto;margin-top: 30px;'>您的账号已被禁用,请联系平台客服</div></div>");
                    response.sendRedirect("http://" + fdomain + "/olv/goBack?err=1");
                    return;
                }

                if (member.getMemberType() == MEMBER_TYPE.ROBOT) {
                    //ResponseUtils.json(response, 500, "该账号类型禁止登录", null);
                    response.getWriter().print("<div style='color:red;font-size:34px;'>该账号类型禁止登录</div>");
                    return;
                }


//				if(GameStore.USER_BEAN_MAP.containsKey(member.getId())) {
//					ResponseUtils.json(response, 500, "此帐号已在它处登陆,请先退出再登陆", null);
//					return;
//				} 


                request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, member.getId());
                MemberBean mb = new MemberBean();
                mb.setParent_uuid(member.getParent());
                mb.setMember_type(member.getMemberType());
                mb.setId(member.getId());
                mb.setUrl("http://" + configService.get().getWx_domain() + "/user/json/i?u=" + member.getMemberId());
                if (StringUtils.isEmpty(member.getHeadpic())) {
                    mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
                } else {
                    mb.setHeadpic(member.getHeadpic());
                }
                mb.setPreTxImg(member.getPreTxImg());
                mb.setPreTxPay(member.getPreTxPay());
                mb.setLastLoginDate(member.getLastLoginDate());
                mb.setLastLoginIp(member.getLastLoginIp());
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

                member.setLastLoginIp(request.getRemoteAddr());
                member.setLastLoginDate(new Date());
                memberService.update(member);

                String[] ps = new String[]{"friendid", "status"};
                Object[] vs = new Object[]{member.getId(), FriendsAdd.Status.wait};
                Long l = memberService.friendsAddCount(ps, vs);
                mb.setUnDoFriendAddCount(l);


                MemberLoginLog log = new MemberLoginLog();
                log.setCreateDate(new Date());
                log.setIp(request.getRemoteAddr());
                log.setMid(member.getMemberId());
                log.setMnickName(member.getNickName());
                log.setMtel(member.getTelphone());
                memberService.saveLoginLog(log);


                ChatStore.USER_BEAN_MAP.put(member.getId(), BeanUtils.memberToBeanSimple(member));

                trajectoryService.generate(mb, "使用微信登陆了游戏(H5)");

                if (!StringUtils.isEmpty(tid)) {
                    TempEt et = tempEtService.get(tid);
                    et.setVal2(member.getId());
                    tempEtService.update(et);
                    //response.setCharacterEncoding("utf-8");
                    //response.setContentType("text/html;charset=utf-8");
                    //response.getWriter().print("<div style='color:#222;font-size:50px;text-align:center;width:100%;margin-top:100px'><img style='width:240px' src='/images/bb2.png'/><div style='margin-top:30px'>登录成功</div><div style='font-size:32px;color:#aaa;   width: 90%;margin: auto auto;margin-top: 30px;'>您已使用微信登录平台，返回APP即可进入</div></div>");
                    response.sendRedirect("http://" + fdomain + "/olv/goBack?te=1&mid=" + member.getId());
                    return;
                } else {
                    //ResponseUtils.json(response, 200,mb , null);
                    response.sendRedirect("http://" + fdomain + "/olv/goBack?mid=" + member.getId());
                }


                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/goQqLogin", method = {RequestMethod.GET})
    public void goQqLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebConfig wc = configService.get();
        String state = UUID.randomUUID().toString();
        request.getSession().setAttribute("qq_state", state);
        String tid = request.getParameter("tid");
        String pid = request.getParameter("pid");
        String callback = "";
        if (!StringUtils.isEmpty(tid)) {
//	    	if (!StringUtils.isEmpty(pid)) {
//	    		callback = "http://"+wc.getWebsiteUrl() + "/olv/wxLoginBack?qcode=" + qcode+"&pid="+pid;
//	    	} else {
//	    		callback = "http://"+wc.getWebsiteUrl() + "/olv/wxLoginBack?qcode=" + qcode;
//	    	}
            callback = "http://" + wc.getQq_domain() + "/olv/qqLoginBack?tid=" + tid;
        } else {
            callback = "http://" + wc.getQq_domain() + "/olv/qqLoginBack";
        }

        String url = "https://graph.qq.com/oauth2.0/authorize?response_type=code" +
                "&client_id=" + wc.getQq_appid() +
                "&redirect_uri=" + URLEncoder.encode(callback) +
                "&state=" + state;
        response.sendRedirect(url);
    }


    @RequestMapping(value = "/qqLoginBack", method = {RequestMethod.GET})
    public void qqLoginBack(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            //
            WebConfig wc = configService.get();
            String code = request.getParameter("code");
            String state = request.getParameter("state");
            String tid = request.getParameter("tid");
            String temp_state = (String) request.getSession().getAttribute("qq_state");
            //
            //
            if ((StringUtils.isEmpty(temp_state)) || (StringUtils.isEmpty(state)) || (!state.equals(temp_state))) {
                return;
            }
            //

            //String backUrl = "http://"+wc.getQq_domain()+"/";
            String backUrl = "http://" + wc.getQq_domain() + "/olv/qqLoginBack";
            String url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code" +
                    "&client_id=" + wc.getQq_appid() +
                    "&client_secret=" + wc.getQq_appkey() +
                    "&code=" + code +
                    "&redirect_uri=" + backUrl;
            String access_token = QQHttpClient.getAccessToken(url);
            url = "https://graph.qq.com/oauth2.0/me?access_token=" + access_token;
            String openid = QQHttpClient.getOpenID(url);
            //
            url = "https://graph.qq.com/user/get_user_info?access_token=" + access_token +
                    "&oauth_consumer_key=" + wc.getQq_appid() +
                    "&openid=" + openid;
            JSONObject jsonObject = QQHttpClient.getUserInfo(url);
            String name = (String) jsonObject.get("nickname");
            String figureurl_qq_2 = (String) jsonObject.get("figureurl_qq_2");
            //
            //


            //
            Member member = memberService.get("qqOpenId", openid);
            if (null == member) {
                //
                //
                member = new Member();
                //注册初始金额
                if (null != wc.getUserRegisterGiveGold()) {
                    member.setMoney(wc.getUserRegisterGiveGold());
                }
                Object $invitor = request.getSession().getAttribute("$invitor");
                if (null != $invitor && !StringUtils.isEmpty($invitor.toString())) {
                    Member parent = memberService.get("memberId", $invitor.toString());
                    if (null != parent) {
                        member.setParent(parent.getId());
                        member.setParentmid(parent.getMemberId());
                        member.setParentPath(parent.getParentPath());
                        request.getSession().removeAttribute("$invitor");
						parent.setOne_level_count(1);
						memberService.update(parent);
//                        memberService.update(new String[]{"one_level_count+"}, new Object[]{1}, "where id='" + parent.getId() + "'");
                    }
                }
                //
                String username = "qq_" + (int) ((Math.random() * 9.0D + 1.0D) * 100000.0D);
                Long l = memberService.count(new String[]{"username"}, new Object[]{username});
                while (null != l && l > 0) {
                    username = "qq_" + (int) ((Math.random() * 9.0D + 1.0D) * 100000.0D);
                    l = memberService.count(new String[]{"username"}, new Object[]{username});
                }

                member.setMemberType(MEMBER_TYPE.USER);
                member.setUsername(username);
                member.setQqOpenId(openid);
                member.setTelphone("");
                member.setPassword(MD5.MD5Encode("123456"));
                member.setRegistIp(request.getRemoteAddr());
                member.setNickName(name);
                member.setCreateDate(new Date());
                TwoDimensionCode handler = new TwoDimensionCode();
                String fn = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
                handler.createQRCode("http://www.baidu.com", request.getRealPath("/images/upload/member") + "/" + fn, "");
                member.setQrCodeImg("/images/upload/member/" + fn);
                member.setMemberId(memberService.generateMemberId() + "");
                member.setStatus(0);
                member.setLastLoginDate(new Date());
                member.setLastLoginIp(request.getRemoteAddr());
                member.setHeadpic(figureurl_qq_2);
                memberService.save(member);

                member.setParentPath(member.getParentPath() + member.getId() + ",");
//                tongjiService.update(new String[]{"todayRegisterCount+"}, new Object[]{1});
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
                mb.setUrl("http://" + configService.get().getWx_domain() + "/user/json/i?u=" + member.getMemberId());

                mb.setLastLoginDate(member.getLastLoginDate());
                mb.setLastLoginIp(member.getLastLoginIp());
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

                MemberLoginLog log = new MemberLoginLog();
                log.setCreateDate(new Date());
                log.setIp(request.getRemoteAddr());
                log.setMid(member.getMemberId());
                log.setMnickName(member.getNickName());
                log.setMtel(member.getTelphone());
                memberService.saveLoginLog(log);
                ChatStore.USER_BEAN_MAP.put(member.getId(), BeanUtils.memberToBeanSimple(member));
                //
                //
                if (!StringUtils.isEmpty(tid)) {
                    //
                    TempEt et = tempEtService.get(tid);
                    et.setVal2(member.getId());
                    if (!StringUtils.isEmpty(et.getVal4())) {
                        Member parent = memberService.get("memberId", et.getVal4());
                        if (null != parent) {
                            member.setParent(parent.getId());
                            mb.setParent(parent.getId());
                            member.setParentmid(parent.getMemberId());
                            member.setParentPath(parent.getParentPath());
                            request.getSession().removeAttribute("$invitor");
							parent.setOne_level_count(1);
							memberService.update(parent);
//                            memberService.update(new String[]{"one_level_count+"}, new Object[]{1}, "where id='" + parent.getId() + "'");
                        }
                    }

                    if (!StringUtils.isEmpty(mb.getParent())) {
                        et.setVal3(wc.getDailiUrlRegisteredTo());
                    }
                    tempEtService.update(et);


                    //response.setCharacterEncoding("utf-8");
                    //response.setContentType("text/html;charset=utf-8");
                    //response.getWriter().print("<div style='color:#222;font-size:50px;text-align:center;width:100%;margin-top:100px'><img style='width:240px' src='/images/bb2.png'/><div style='margin-top:30px'>登录成功</div><div style='font-size:32px;color:#aaa;   width: 90%;margin: auto auto;margin-top: 30px;'>您已使用微信登录平台，返回APP即可进入</div></div>");
                    response.sendRedirect("/olv/goBack?te=1");
                    //
                    return;
                } else {
                    //
                    if (!StringUtils.isEmpty(mb.getParent())) {
                        //ResponseUtils.json(response, 200,mb ,wc.getDailiUrlRegisteredTo() );
                        response.sendRedirect("/olv/goBack?url=" + wc.getDailiUrlRegisteredTo());
                    } else {
                        //response.sendRedirect("http://"+wc.getWebsiteUrl()+"/");
                        response.sendRedirect("/olv/goBack");
                    }
                    //
                }
                memberService.update(member);
                trajectoryService.generate(mb, "使用QQ注册并登陆了游戏");
            } else {
                //
                if (null != member.getStatus() && member.getStatus().intValue() == 1) {
                    //ResponseUtils.json(response, 500, "帐号禁用", null);
                    //response.setCharacterEncoding("utf-8");
                    //response.setContentType("text/html;charset=utf-8");
                    //response.getWriter().print("<div style='color:#222;font-size:50px;text-align:center;width:100%;margin-top:100px'><img style='width:240px' src='/images/as4.png'/><div style='margin-top:30px'>登录失败</div><div style='font-size:32px;color:#aaa;   width: 90%;margin: auto auto;margin-top: 30px;'>您的账号已被禁用,请联系平台客服</div></div>");
                    response.sendRedirect("/olv/goBack?err=1");
                    return;
                }

                if (member.getMemberType() == MEMBER_TYPE.ROBOT) {
                    //ResponseUtils.json(response, 500, "该账号类型禁止登录", null);
                    response.getWriter().print("<div style='color:red;font-size:34px;'>该账号类型禁止登录</div>");
                    return;
                }

                request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, member.getId());
                MemberBean mb = new MemberBean();
                mb.setParent_uuid(member.getParent());
                mb.setMember_type(member.getMemberType());
                mb.setId(member.getId());
                mb.setUrl("http://" + configService.get().getQq_domain() + "/user/json/i?u=" + member.getMemberId());
                if (StringUtils.isEmpty(member.getHeadpic())) {
                    mb.setHeadpic("/img_sys/defaultHeadPic.jpg");
                } else {
                    mb.setHeadpic(member.getHeadpic());
                }
                mb.setPreTxImg(member.getPreTxImg());
                mb.setPreTxPay(member.getPreTxPay());
                mb.setLastLoginDate(member.getLastLoginDate());
                mb.setLastLoginIp(member.getLastLoginIp());
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

				member.setLastLoginIp(request.getRemoteAddr());
				member.setLastLoginDate(new Date());

                String[] ps = new String[]{"friendid", "status"};
                Object[] vs = new Object[]{member.getId(), FriendsAdd.Status.wait};
                Long l = memberService.friendsAddCount(ps, vs);
                mb.setUnDoFriendAddCount(l);


                MemberLoginLog log = new MemberLoginLog();
                log.setCreateDate(new Date());
                log.setIp(request.getRemoteAddr());
                log.setMid(member.getMemberId());
                log.setMnickName(member.getNickName());
                log.setMtel(member.getTelphone());
                memberService.saveLoginLog(log);


                ChatStore.USER_BEAN_MAP.put(member.getId(), BeanUtils.memberToBeanSimple(member));

                trajectoryService.generate(mb, "使用QQ登陆了游戏(H5)");


                //
                if (!StringUtils.isEmpty(tid)) {
                    //
                    //
                    TempEt et = tempEtService.get(tid);
                    et.setVal2(member.getId());
                    tempEtService.update(et);
                    //response.setCharacterEncoding("utf-8");
                    //response.setContentType("text/html;charset=utf-8");
                    //response.getWriter().print("<div style='color:#222;font-size:50px;text-align:center;width:100%;margin-top:100px'><img style='width:240px' src='/images/bb2.png'/><div style='margin-top:30px'>登录成功</div><div style='font-size:32px;color:#aaa;   width: 90%;margin: auto auto;margin-top: 30px;'>您已使用微信登录平台，返回APP即可进入</div></div>");
                    response.sendRedirect("/olv/goBack?te=1");
                    return;
                } else {
                    //
                    //
                    //ResponseUtils.json(response, 200,mb , null);
                    response.sendRedirect("/olv/goBack");
                }


                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
