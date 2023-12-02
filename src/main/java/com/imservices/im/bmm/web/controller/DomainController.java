package com.imservices.im.bmm.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.service.FileSystemService;
import com.imservices.im.bmm.utils.oss.UploadResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller("DomainController")
@RequestMapping(value = "/config")
public class DomainController {

    @Resource
    private FileSystemService fileSystemService;
    private final static String ossPath = "config_sys";
    private final static String ossFileName = "domains.txt";

    @RequestMapping(value = "/createConfigDomains",method = {RequestMethod.POST})
    private void createConfigDomains(@RequestBody JSONObject jsonObject){
        //{‘httpDomains’:[{},{}],
        //  ‘websocketDomains’:[{},{}]
        // ‘mediaDomains’:[{},{}]
        //}w4q
        try {
            byte[] encryptBytes =  jsonObject.toString().getBytes(StandardCharsets.UTF_8);
            InputStream in = new ByteArrayInputStream(encryptBytes);
            UploadResp resp =  fileSystemService.uploadObject(in,ossFileName,ossPath, (long) in.available());
            log.debug("oss上传结束:{}",JSONObject.toJSONString(resp));
        } catch (Exception e) {
            log.error("===,AppDomainModel:{}",e);
        }
    }



}
