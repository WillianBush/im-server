package com.imservices.im.bmm.mq.receiver;

import com.alibaba.fastjson.JSON;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@Component
@Slf4j
public class GroupExceptionMessageReceiver {

    @Autowired
    private SendUtils sendUtils;

    @RabbitListener(queues = "im.message.group.exception.queue")//监听的队列名称
    public void process(String obj) {
        try {
            HashMap map = JSON.parseObject(obj, HashMap.class);
            String userId = (String) map.get("userId");
            Message message = JSON.parseObject(map.get("message").toString(), Message.class);
            log.info("队列消息....");
            sendUtils.sendRemoteGroupMessage(userId, message);
//            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("GroupExceptionMessageReceiver：{}", e);
        }
    }
}