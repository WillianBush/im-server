package com.imservices.im.bmm.service;

import com.imservices.im.bmm.bean.Pager;
import com.imservices.im.bmm.entity.HomepageEntity;
import com.imservices.im.bmm.entity.HomepageUserEntity;

import java.util.List;

public interface HomePageUserService {

    List<HomepageUserEntity> getHomepageUserInfo(String member_id);

}
