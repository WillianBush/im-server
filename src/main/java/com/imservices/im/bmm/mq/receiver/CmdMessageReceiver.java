package com.imservices.im.bmm.mq.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CmdMessageReceiver {

    @Autowired
    private SendUtils sendUtils;

    @RabbitListener(queues = {"#{T(com.imservices.im.bmm.mq.RabbitmqConfig).getCmdMessageQueueName()}${server.port}"})//监听的队列名称
    public void process(String obj) {

        try {
            Map map=JSON.parseObject(obj, HashMap.class);
            String id=map.get("id").toString();
            Message message = JSON.parseObject(map.get("message").toString(),Message.class);
            List<WebSocketSession> list= SessionStore.USERID_WS_MAP_GET_ByUid(id);
            list.forEach(ws->{
                sendUtils.send(id,message);
            });
        } catch (Exception e){
            log.error("CmdMessageReceiver：{}", e);
        }
    }
}
