package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.utils.web.ResponseUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller("HealthCheckController")
@RequestMapping(value = {"/user/health"})
@AllArgsConstructor
@CrossOrigin
public class HealthCheckController {

    @RequestMapping(value = "/check", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public void verifySmsCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResponseUtils.json(response, 200, "ok", null);
    }
}
