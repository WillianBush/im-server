package com.imservices.im.bmm.mq.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.bean.ChatTxtBean;
import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.websocket.cmd.UserChatCmd;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
@Slf4j
public class MessageReceiver {

    @Autowired
    private UserChatCmd userChatCmd;
    @Value("${server.port}")
    private String serverPort;

    @RabbitListener(queues = {"#{T(com.imservices.im.bmm.mq.RabbitmqConfig).getUserQueueName()}${server.port}"})
//监听的队列名称
    public void process(String obj) {

        try {
            ChatTxtBean bean = JSON.parseObject(obj, ChatTxtBean.class);
            log.error("MessageReceiver:{}", JSONObject.toJSONString(bean));
            List<WebSocketSession> list = SessionStore.USERID_WS_MAP_GET_ByUid(bean.getToUid());
            log.info("MessageReceiver-session:{}", list.size());
            //只有存在于当前服务器中，才进行消息推送
            if (list.size() > 0) {
                bean.setTxt(bean.getTxt());
                userChatCmd.sendTXTToByMq(bean);
//                channel.basicAck(deliveryTag,false);
            }
        } catch (Exception e) {
            log.error("MessageReceiver", e);
        }
    }

}