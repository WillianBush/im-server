package com.imservices.im.bmm.web.controller;

import com.imservices.im.bmm.constant.MemberConstant;
import com.imservices.im.bmm.entity.AppVersionEntity;
import com.imservices.im.bmm.entity.CommodityCategoryEntity;
import com.imservices.im.bmm.entity.CommodityEntity;
import com.imservices.im.bmm.service.AppVersionService;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.utils.web.ResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequestMapping("/user/appversion/json")
@RestController
@CrossOrigin
@AllArgsConstructor
@Slf4j
public class AppVerisonController {

    private final AppVersionService appVersionService;

    private final RedisService redisService;

    @RequestMapping(value = "/getAppVersion", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public void getAppVersion(@RequestParam String site_id,@RequestParam String device_type,@RequestParam String app_id,HttpServletResponse response){

        if (StringUtils.isEmpty(site_id) || StringUtils.isEmpty(device_type) ||  StringUtils.isEmpty(app_id)) {
            ResponseUtils.json(response, 500, "params error", null);
            return;
        }

        try {
            AppVersionEntity appVersionEntity =  redisService.getAndSet(MemberConstant.APP_VERSION+site_id+":"+device_type+":"+app_id,15L,AppVersionEntity.class,()->
                    appVersionService.get(site_id, device_type, app_id)
            );
            if (appVersionEntity == null) {
                ResponseUtils.json(response, 500, "APP不存在", null);
                return;
            }
            ResponseUtils.json(response, 200, appVersionEntity, null);
        }catch (Exception e) {
            log.error("getAppVersion 异常",e);
            ResponseUtils.json(response, 200, null, null);
        }

    }

    @RequestMapping(value = "/getCommodity", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public void getCommodity(HttpServletResponse response) {
        try {
            List<CommodityEntity> list = appVersionService.getCommodity();
            ResponseUtils.json(response, 200, list, null);
        } catch (Exception e) {
            log.error("getAppVersion 异常",e);
            ResponseUtils.json(response, 200, null, null);
        }
    }

    @RequestMapping(value = "/getCommodityCategory", method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public void getCommodityCategory(HttpServletResponse response) {
        try {
            List<CommodityCategoryEntity> list = appVersionService.getCommodityCategory();
            ResponseUtils.json(response, 200, list, null);
        } catch (Exception e) {
            log.error("getAppVersion 异常",e);
            ResponseUtils.json(response, 200, null, null);
        }
    }
}
