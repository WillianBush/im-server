package com.imservices.im.bmm.service;

import com.imservices.im.bmm.entity.AppVersionEntity;
import com.imservices.im.bmm.entity.CommodityCategoryEntity;
import com.imservices.im.bmm.entity.CommodityEntity;

import java.util.List;

public interface AppVersionService {
    AppVersionEntity get(String siteId, String os,String appId) ;

    List<AppVersionEntity> getAppVersion(String appId);

    List<CommodityEntity> getCommodity();
    List<CommodityCategoryEntity> getCommodityCategory();

}
