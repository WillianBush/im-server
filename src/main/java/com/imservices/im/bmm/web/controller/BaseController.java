package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.service.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
@CrossOrigin
public class BaseController {

    @Resource
    public MemberService memberService;

    public Member getUserInfo(HttpServletRequest request) throws Exception {
        Object memberId = request.getSession().getAttribute(MemberConstant.MEMBERIDSESSION);
        if (null == memberId) {
            String uid = request.getHeader("x-access-uid");
            if (StringUtils.isEmpty(uid)) {
                throw new Exception("no login");
            }
            if (uid.endsWith("#")) {
                uid = uid.replace("#", "");
            }
            if (uid.endsWith("#/")) {
                uid = uid.replace("#/", "");
            }
            return memberService.get(uid);
        }
        return memberService.get(memberId.toString());
    }
}
