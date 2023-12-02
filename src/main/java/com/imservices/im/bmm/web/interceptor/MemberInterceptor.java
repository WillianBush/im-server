package com.imservices.im.bmm.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.bean.store.ChatStoreComponent;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.service.EmployeeService;
import com.imservices.im.bmm.service.IpListService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.utils.web.BeanUtils;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@SuppressWarnings("all")
@Slf4j
public class MemberInterceptor extends HandlerInterceptorAdapter {

    private MemberService memberService;

    private ChatStoreComponent chatStoreComponent;

    private EmployeeService employeeService;

    private IpListService ipListService;


    public MemberInterceptor(MemberService memberService, ChatStoreComponent chatStoreComponent, EmployeeService employeeService, IpListService ipListService) {
        this.memberService = memberService;
        this.chatStoreComponent = chatStoreComponent;
        this.employeeService =employeeService ;
        this.ipListService =ipListService;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            return true;
        }
        //
        AuthPassport authPassportAnnotaction = (AuthPassport) ((HandlerMethod) handler)
                .getMethodAnnotation(AuthPassport.class);
        if (authPassportAnnotaction == null) {
            boolean isIpBlacked = ipListService.isInBlackIps(request);
            if (isIpBlacked){
                response.setStatus(HttpStatus.SC_BAD_GATEWAY);
                ResponseUtils.json(response, 502, "网络异常,请联系管理员", null);
                return false;
            }
            String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
            if (StringUtils.isEmpty(MEMBERID)) {
                String uid = request.getHeader("x-access-uid");
                if (StringUtils.isEmpty(uid)){
                    return true;
                }
                if (uid.endsWith("#")) {
                    uid = uid.replace("#", "");
                }
                if (uid.endsWith("#/")) {
                    uid = uid.replace("#/", "");
                }
                request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, uid);
            }
            return true;
        }
//        log.info("===========================MemberInterceptor........");

        //如果配置为异步请求
        if (authPassportAnnotaction.value()) {
            String MEMBERID = (String) request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
            if (StringUtils.isEmpty(MEMBERID)) {
                if (!StringUtils.isEmpty(request.getHeader("x-access-uid"))) {
                    String uid = request.getHeader("x-access-uid");
                    if (uid.endsWith("#")) {
                        uid = uid.replace("#", "");
                    }
                    if (uid.endsWith("#/")) {
                        uid = uid.replace("#/", "");
                    }
                    request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, uid);

					Member member = memberService.get(uid);
                    if (member == null) {
                        log.error("session过期,uid:{}",uid);
                        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                        ResponseUtils.json(response, 401, "请重新登陆", null);
                        return false;
                    }
                    boolean isEnableRequest = ipListService.enableRequest(member,request);
                    if (!isEnableRequest){
                        log.error("拉黑用户禁止访问,member:{}", JSONObject.toJSONString(member));
                        response.setStatus(HttpStatus.SC_BAD_GATEWAY);
                        ResponseUtils.json(response, 502, "网络异常,请联系管理员", null);
                        return false;
                    }
                    member.setLastLoginDate(new Date());
                    member.setLastLoginIp(request.getRemoteAddr());
                    memberService.update(member);
                    chatStoreComponent.putMemberBean(uid, BeanUtils.memberToBeanSimple(member));
                    return true;
                }
                ResponseUtils.json(response, 1000 - 599, "", null);
                return false;
            } else {
				Member member = memberService.get(MEMBERID);

                if (member == null) {
                    log.error("session过期,uid:{}",MEMBERID);
                    response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                    ResponseUtils.json(response, 401, "请重新登陆", null);
                    return false;
                }

                boolean isEnableRequest = ipListService.enableRequest(member,request);
                if (!isEnableRequest){
                    log.error("拉黑用户禁止访问,member:{}", JSONObject.toJSONString(member));
                    response.setStatus(HttpStatus.SC_BAD_GATEWAY);
                    ResponseUtils.json(response, 502, "网络异常,请联系管理员", null);
                    return false;
                }
                member.setLastLoginDate(new Date());
                member.setLastLoginIp(request.getRemoteAddr());
                memberService.update(member);
                request.getSession().setAttribute(MemberConstant.MEMBERIDSESSION, member.getId());
                chatStoreComponent.putMemberBean(member.getId(), BeanUtils.memberToBeanSimple(member));
                return true;
            }
        }
        return false;
    }


}

