package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.entity.AppVersionEntity;
import com.imservices.im.bmm.entity.CommodityCategoryEntity;
import com.imservices.im.bmm.entity.CommodityEntity;
import com.imservices.im.bmm.service.AppVersionService;
import com.imservices.im.bmm.service.BaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
@SuppressWarnings("all")
@AllArgsConstructor
@Slf4j
public class AppVersionServiceImpl implements AppVersionService {

    private final BaseService baseService;

    public AppVersionEntity get(String siteId, String os, String appId) {
        try {
            List<AppVersionEntity> dataList = this.getAppVersion(appId);
            for (AppVersionEntity appVersionEntity : dataList) {
//                log.info("查询条件Appverison-id:{}", appId);
//                log.info("查询结果Appverison-id:{}", appVersionEntity.getApp_id());
//                log.info("查询条件Appverison-siteId:{}", siteId);
//                log.info("查询结果Appverison-siteId:{}", appVersionEntity.getOrg_id());
//                log.info("查询条件Appverison-devicetype:{}", os);
//                log.info("查询结果Appverison-devicetype:{}", os);
                if ( !StringUtils.isEmpty(appVersionEntity.getDevice_type()) && appVersionEntity.getDevice_type().equals(os)){
                    if (!StringUtils.isEmpty(appVersionEntity.getOrg_id()) && appVersionEntity.getOrg_id().equals(siteId)) {
                        return appVersionEntity;
                    }
                }
            }
        }catch (Exception e) {
            log.error("",e);
        }

        return null;
    }

    @Override
    public List<AppVersionEntity> getAppVersion(String appId)  {
        try {
            return baseService.getList(AppVersionEntity.class,"app_id", appId);
        } catch (Exception e) {
            log.error("",e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<CommodityEntity> getCommodity() {
        try {
            return baseService.getAll(CommodityEntity.class);
        } catch (Exception e) {
            log.error("",e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<CommodityCategoryEntity> getCommodityCategory() {
        try {
            return baseService.getAll(CommodityCategoryEntity.class);
        } catch (Exception e) {
            log.error("",e);
        }
        return Collections.emptyList();
    }
}
