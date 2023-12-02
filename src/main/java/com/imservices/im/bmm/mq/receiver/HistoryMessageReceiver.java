package com.imservices.im.bmm.mq.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.entity.MsgHistoryEntity;
import com.imservices.im.bmm.service.ChatService;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@AllArgsConstructor
public class HistoryMessageReceiver {

    private ChatService chatService;

    @RabbitListener(queues = "im.message.history.msg.queue")//监听的队列名称
    public void process(String obj) {
        try {
            MsgHistoryEntity msgHistoryEntity = JSON.parseObject(obj, MsgHistoryEntity.class);
            chatService.saveMsgHistory(msgHistoryEntity);
//            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            log.error("HistoryMessageReceiver :{}",e);
        }
    }

}