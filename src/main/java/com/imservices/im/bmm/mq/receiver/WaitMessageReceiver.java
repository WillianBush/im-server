package com.imservices.im.bmm.mq.receiver;

import com.alibaba.fastjson.JSON;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
public class WaitMessageReceiver {

    @Autowired
    private SendUtils sendUtils;

    @RabbitListener(queues = "im.message.waitmsg.queue")//监听的队列名称
    public void process(String obj) {
        try {
            HashMap map = JSON.parseObject(obj, HashMap.class);
            Message msg = JSON.parseObject(map.get("message").toString(), Message.class);
            String uid = map.get("userId").toString();
            sendUtils.saveWaitMsg(msg, uid);
//            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            log.error("WaitMessageReceiver :{}",e);
        }
    }

}