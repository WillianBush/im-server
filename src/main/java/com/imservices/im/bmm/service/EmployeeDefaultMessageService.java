package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.EmployeeDefaultMessage;

import java.util.List;

public interface EmployeeDefaultMessageService {

    Pager findByPager(String[] properties, Object[] vals, Pager Pager) throws Exception;

    void delete(EmployeeDefaultMessage o) throws Exception;

    void update(EmployeeDefaultMessage o) throws Exception;

//    void update(String[] ps, Object[] vs, String where) throws Exception;

    void save(EmployeeDefaultMessage o) throws Exception;

    EmployeeDefaultMessage get(String id) throws Exception;

    void delete(String[] ids) throws Exception;

    List<EmployeeDefaultMessage> getList(String[] ps, Object[] vs) throws Exception;

    Object sum(String hql) throws Exception;

    void delete(String id) throws Exception;

    Long count(String[] ps, Object[] vs) throws Exception;

    EmployeeDefaultMessage get(String p, String v) throws Exception;

    List<EmployeeDefaultMessage> getMyHelloMessage(String memberId) throws Exception;


    EmployeeDefaultMessage getMyHelloMessage(String memberId,String InviteCode) throws Exception;

    boolean isEmployId(String memberId) throws Exception;
}
