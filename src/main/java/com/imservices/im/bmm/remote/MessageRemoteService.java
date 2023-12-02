package com.imservices.im.bmm.remote;

import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.entity.Resp;
import com.imservices.im.bmm.exception.RRException;
import com.imservices.im.bmm.utils.OKhttpUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MessageRemoteService {

    private final DiscoveryClient discoveryClient;
    private final OKhttpUtil oKhttpUtil;

    private List<ServiceInstance> getRemoteServices(String serviceName) {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
        int serviceInstancesSize = serviceInstances.size();
        if (serviceInstances.isEmpty()) {
            log.error("registerServices 服务未注册;serviceInstancesSize:{}", serviceInstancesSize);
            throw new RRException("MessageRemoteException");
        }
        return serviceInstances;
    }

    /**
     * 同步自己的消息
     * @param serviceName
     * @param remoteMessage
     */
    public boolean requestRemoteSefMessage(String serviceName,RemoteMessage remoteMessage){
        List<ServiceInstance> remoteInstants = getRemoteServices(serviceName);
        boolean res =false;
        for (ServiceInstance serviceInstant : remoteInstants) {
            try {
                if (StringUtils.equals(serviceInstant.getHost(),getLocalIP())){
                    continue;
                }
                log.info("发送远程消息-requestRemoteSefMessage service-host:{};local-ip:{}",serviceInstant.getHost(),getLocalIP());
                String content = oKhttpUtil.httpPostJson(serviceInstant.getUri().toString()+ RemoteConstants.SelfMessage, remoteMessage);
                Resp<Integer> resp = JSONObject.parseObject(content,Resp.class);
                if (resp.getCode().equals(Resp.ok)){
                    res = true;
                }
            } catch (IOException e) {
                log.error("requestRemoteSefMessage,serviceUri:{};RemoteMessage:{};\nlocal-ip:{}",serviceInstant.getUri().toString() + RemoteConstants.GroupMessage, JSONObject.toJSONString(remoteMessage),getLocalIP(),e);
            }
        }
        return res;
    }


    /**
     * 给群组发送消息
     * @param serviceName
     * @param remoteMessage
     */
    public boolean requestRemoteGroupMessage(String serviceName, RemoteMessage remoteMessage){
        List<ServiceInstance> remoteInstants = getRemoteServices(serviceName);
        boolean res =false;
        for (ServiceInstance serviceInstant : remoteInstants) {
            try {
                if (StringUtils.equals(serviceInstant.getHost(),getLocalIP())){
                    continue;
                }
                log.info("发送远程消息-requestRemoteGroupMessage service-host:{};local-ip:{}",serviceInstant.getHost(),getLocalIP());
                String content = oKhttpUtil.httpPostJson(serviceInstant.getUri().toString() + RemoteConstants.GroupMessage, remoteMessage);
                Resp<Integer> resp = JSONObject.parseObject(content,Resp.class);
                if (resp.getCode().equals(Resp.ok)){
                    res = true;
                }
            } catch (IOException e) {
                log.error("requestRemoteGroupMessage,serviceUri:{};RemoteMessage:{};\nlocal-ip:{}",serviceInstant.getUri().toString() + RemoteConstants.GroupMessage, JSONObject.toJSONString(remoteMessage),getLocalIP(),e);
            }
        }
        return res;
    }

    public static String getLocalIP() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        }catch (Exception e){
            log.error("getLocalIP:",e);
        }
        return "";
    }

}
