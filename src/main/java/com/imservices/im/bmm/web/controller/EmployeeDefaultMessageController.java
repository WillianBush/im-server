package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.annotation.AuthPassport;
import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.Employee;
import com.imservices.im.bmm.entity.EmployeeDefaultMessage;
import com.imservices.im.bmm.entity.Member;
import com.imservices.im.bmm.service.EmployeeDefaultMessageService;
import com.imservices.im.bmm.service.EmployeeService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@RequestMapping("/user/employeeDefaultMessage/json")
@RestController
@CrossOrigin
public class EmployeeDefaultMessageController extends BaseController{

    private final EmployeeDefaultMessageService employeeDefaultMessageService;

    private final  EmployeeService employeeService;

    private final  RedisService redisService;

    @Autowired
    public EmployeeDefaultMessageController(EmployeeDefaultMessageService employeeDefaultMessageService, EmployeeService employeeService, RedisService redisService) {
        this.employeeDefaultMessageService = employeeDefaultMessageService;
        this.employeeService = employeeService;
        this.redisService = redisService;
    }

    @AuthPassport
    @RequestMapping(value = "/isEmployee",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void isEmployee(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Member user = super.getUserInfo(request);
        Employee employee  = employeeService.get("member_id",user.getMemberId());
        if (employee == null) {
            ResponseUtils.json(response,ResponseUtils.STATUS.success,"No",null);
        }else {
            ResponseUtils.json(response,ResponseUtils.STATUS.success,"Yes",null);
        }
    }

    @AuthPassport
    @RequestMapping(value = "/getMyHelloMessage",method = {RequestMethod.POST,RequestMethod.OPTIONS,RequestMethod.GET})
    public void getMyHelloMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Member user = super.getUserInfo(request);
        Employee employee  = employeeService.get("member_id",user.getMemberId());
        if (employee == null) {
            ResponseUtils.json(response,ResponseUtils.STATUS.success,"",null);
            return;
        }
        List<EmployeeDefaultMessage> dataList = employeeDefaultMessageService.getMyHelloMessage(user.getMemberId());
        if (dataList == null || dataList.isEmpty()) {
            ResponseUtils.json(response,200,"",null);
            return;
        }

        ResponseUtils.json(response,200, Collections.singletonMap("rows",dataList),null);
    }

//    @AuthPassport
//    @RequestMapping(value = "/add",method = {RequestMethod.POST,RequestMethod.OPTIONS})
//    public void add(HttpServletRequest request,HttpServletResponse response,@RequestBody EmployeeDefaultMessage employeeDefaultMessage) throws Exception {
//        Member user = super.getUserInfo(request);
//        EmployeeDefaultMessage data = employeeDefaultMessageService.getMyHelloMessage(user.getMemberId());
//        if (data != null) {
//            ResponseUtils.json(response,ResponseUtils.STATUS.error,"数据已存在",null);
//            return;
//        }
//        Employee employee  = employeeService.get("member_id",user.getMemberId());
//        if (employee == null) {
//            ResponseUtils.json(response,ResponseUtils.STATUS.error,"403",null);
//            return;
//        }
//        employeeDefaultMessage.setEmployee_id(employee.id);
//        employeeDefaultMessage.setMember_id(user.getMemberId());
//        employeeDefaultMessageService.save(employeeDefaultMessage);
//        redisService.setDays(MemberConstant.REDIS_EMPLOYEE_DEFAULT_MESSAGE + employee.getId(), JSONObject.toJSONString(employeeDefaultMessage),60);
//        ResponseUtils.json(response,200,employeeDefaultMessage,null);
//    }

    @AuthPassport
    @RequestMapping(value = "/update",method = {RequestMethod.POST,RequestMethod.OPTIONS})
    public void update(HttpServletRequest request,HttpServletResponse response,@RequestBody EmployeeDefaultMessage employeeDefaultMessage) throws Exception {
        Member user = super.getUserInfo(request);
        Employee employee  = employeeService.get("member_id",user.getMemberId());
        if (employee == null) {
            ResponseUtils.json(response,ResponseUtils.STATUS.error,"403",null);
            return;
        }
        if (StringUtils.isEmpty(employeeDefaultMessage.getId())){
            ResponseUtils.json(response,ResponseUtils.STATUS.error,"403",null);
            return;
        }
        EmployeeDefaultMessage data = employeeDefaultMessageService.get("id",employeeDefaultMessage.getId());
        if (data == null) {
            ResponseUtils.json(response,ResponseUtils.STATUS.error,"403",null);
            return;
        }
        data.setMsg_1(employeeDefaultMessage.getMsg_1());
        data.setMsg_2(employeeDefaultMessage.getMsg_2());
        data.setMsg_3(employeeDefaultMessage.getMsg_3());
        data.setPicture_1(employeeDefaultMessage.getPicture_1());
        data.setPicture_2(employeeDefaultMessage.getPicture_2());
        data.setPicture_3(employeeDefaultMessage.getPicture_3());
        data.setPicture_4(employeeDefaultMessage.getPicture_4());
        data.setPicture_5(employeeDefaultMessage.getPicture_5());
        employeeDefaultMessageService.update(data);
        redisService.del(MemberConstant.REDIS_EMPLOYEE_DEFAULT_MESSAGE + user.getMemberId());
        ResponseUtils.json(response,200,data,null);
    }
}
