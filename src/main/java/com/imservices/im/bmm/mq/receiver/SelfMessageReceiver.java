package com.imservices.im.bmm.mq.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.bean.ChatTxtBean;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.mq.RabbitmqConfig;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class SelfMessageReceiver {

    private SendUtils sendUtils;

    @RabbitListener(queues = {"#{T(com.imservices.im.bmm.mq.RabbitmqConfig).getSelfMsgQueueName()}${server.port}"})//监听的队列名称
    public void process(String obj) {
        try {
            HashMap map = JSON.parseObject(obj, HashMap.class);
            String uid = (String) map.get("uid");
            Message message = JSON.parseObject(map.get("message").toString(),Message.class) ;
            log.info("获取自己的消息 SelfMessageReceiver:{}; ip:{}",map.get("message").toString(), RabbitmqConfig.getLocalIP());
            ChatTxtBean bean = null;
            try {
                bean = JSON.parseObject(obj, ChatTxtBean.class);
            }catch (Exception e) {
                log.error("",e);
                try {
                    bean =JSONObject.parseObject( obj,ChatTxtBean.class);
                }catch (Exception e1) {
                    log.error("",e1);
                }
            }

            if (null == bean){
               log.error("消息转换失败SelfMessageReceiver,ChatTxtBean = null ; message.getBody():{}",message.getBody());
               return;
            }
            String fromUid = bean.getFromUid();
            if (!StringUtils.equals(fromUid,uid)){
                return;
            }
            List<WebSocketSession> sessionWsMap = SessionStore.USERID_WS_MAP_GET_ByUid(uid);
            //只有存在于当前服务器中，才进行消息推送
            for(WebSocketSession ws:sessionWsMap){
                sendUtils.send(ws, message);
            }
        } catch (Exception e) {
            log.error("SelfMessageReceiver：", e);
        }
    }
}
