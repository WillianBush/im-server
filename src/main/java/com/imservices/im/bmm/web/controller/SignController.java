package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@Controller("SignController")
@RequestMapping(value = "/user/signin")
@CrossOrigin
@Slf4j
@AllArgsConstructor
public class SignController {

    @AuthPassport
    @RequestMapping(value = "/isToDaySignIn",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void isToDaySignIn(HttpServletRequest request, HttpServletResponse response) {
        ResponseUtils.json(response, 200, Collections.singletonMap("useSignIn",0), null);
    }
}
