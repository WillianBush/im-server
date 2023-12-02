package com.imservices.im.bmm.service;

import com.imservices.im.bmm.entity.Member;

public interface AfterRegisterService {

    @Deprecated
    void afterRegister(Member user) throws Exception;

    void afterRegister(Member user,String InviteCode) throws Exception;
}
