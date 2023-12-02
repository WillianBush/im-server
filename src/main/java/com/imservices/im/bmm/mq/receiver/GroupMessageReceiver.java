package com.imservices.im.bmm.mq.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class GroupMessageReceiver {

    @Autowired
    private SendUtils sendUtils;

    @RabbitListener(queues = {"#{T(com.imservices.im.bmm.mq.RabbitmqConfig).getGroupQueueName()}${server.port}"})//监听的队列名称
    public void process(String obj) {
        HashMap map= JSON.parseObject(obj,HashMap.class);
        log.error("GroupMessageReceiver:{}", JSONObject.toJSONString(map));
        String userId= (String) map.get("userId");
        Message message=JSON.parseObject(map.get("message").toString(),Message.class);
        sendUtils.processRemoteGroupMessage(userId,message);
//        log.info("消费者收到消息  : " + obj);
    }

}