package com.imservices.im.bmm.remote;

import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.entity.Resp;
import com.imservices.im.bmm.websocket.Message;
import com.imservices.im.bmm.websocket.Utils.SendUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("remote")
@AllArgsConstructor
public class MessageRemoteClient {

    private SendUtils sendUtils;

    @PostMapping("self")
    public Resp<Integer> sendSelf(@RequestBody RemoteMessage remoteMessage){
        List<WebSocketSession> sessionWsMap = SessionStore.USERID_WS_MAP_GET_ByUid(remoteMessage.getToUid());
//        log.info("收到远程消息推送,sessionWsMap.size:{}",sessionWsMap.size());
        //只有存在于当前服务器中，才进行消息推送
        for(WebSocketSession ws:sessionWsMap){
            Message message = remoteMessage.getMessage();
            if (StringUtils.isEmpty(message.getCMD())){
                if (StringUtils.isEmpty(remoteMessage.getCMD())){
                    message.setCMD(Message.CMD_ENUM.USER_CHAT_MESSAGE.name());
                }else {
                    message.setCMD(remoteMessage.getCMD());
                }

            }
            sendUtils.send(ws, remoteMessage.getMessage());
        }
        if (sessionWsMap.isEmpty()){
            return Resp.error(0);
        }
        return Resp.ok(sessionWsMap.size());
    }

    @PostMapping("group")
    public Resp<Integer> sendGroup(@RequestBody RemoteMessage remoteMessage){
        List<WebSocketSession> sessionWsMap = SessionStore.USERID_WS_MAP_GET_ByUid(remoteMessage.getToUid());
//        log.info("收到远程消息推送,sessionWsMap.size:{}",sessionWsMap.size());
        //只有存在于当前服务器中，才进行消息推送
        for(WebSocketSession ws:sessionWsMap){
            Message message = remoteMessage.getMessage();
            if (StringUtils.isEmpty(message.getCMD())){
                if (StringUtils.isEmpty(remoteMessage.getCMD())){
                    message.setCMD(Message.CMD_ENUM.USER_CHAT_MESSAGE.name());
                }else {
                    message.setCMD(remoteMessage.getCMD());
                }
            }
            sendUtils.send(ws, remoteMessage.getMessage());
        }
        if (sessionWsMap.isEmpty()){
            return Resp.error(0);
        }
        return Resp.ok(sessionWsMap.size());
    }

}
