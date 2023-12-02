package com.imservices.im.bmm.websocket.core;

import com.imservices.im.bmm.bean.store.SessionStore;
import com.imservices.im.bmm.utils.redis.RedisService;
import com.imservices.im.bmm.websocket.WebSocketSessionDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Service
@AllArgsConstructor
public class WsSessionService {


    private RedisService redisService;


    public void removeSession(WebSocketSession session){
        if (SessionStore.SESSIONID_MEMBERID_LIST_MAP.containsKey(session.getId())) {
//            log.info("开始删除ws session");
            //
            String keyWithDevice = SessionStore.SESSIONID_MEMBERID_LIST_MAP.get(session.getId());
            String key = keyWithDevice.split("#")[0];
//            log.info("开始删除ws session, key:{},arg0.getId():{}",key,arg0.getId());
            /**旧代码**/
//            SessionStore.USERID_WS_MAP_REMOVE(key);
//            SessionStore.WS_USERID_MAP.remove(arg0);
            /**将缓存在redis的请求地址删除**/
            redisService.hDelete(SessionStore.REDIS_WSS_KEY,key.split("#")[0]);
            redisService.hDelete(SessionStore.ONLINE_MEMBER,key);
            Long delete=redisService.hDelete(key,session.getId());
//            log.info("删除结束 ws session, key:{},arg0.getId():{},delete:{}",key,arg0.getId(),delete);
            /**新代码**/
            try {
                SessionStore.removerUserWebSocketList(key,session.getId());
            }catch (Exception e) {
//                log.error("",e);
            }
            try {
                SessionStore.USERID_SESSION_WS_MAP.remove(keyWithDevice);
            }catch (Exception e) {
//                log.error("",e);
            }

            try {
                SessionStore.SESSIONID_MEMBERID_LIST_MAP.remove(session.getId());
            }catch (Exception e) {
//                log.error("",e);
            }
            try {
                SessionStore.WS_USERID_MAP.remove(key);
            }catch (Exception e) {
//                log.error("",e);
            }
        }
        WebSocketSessionDto webSocketSessionDto = new WebSocketSessionDto(session);
        if (null ==  webSocketSessionDto.getSessionId()){
            return;
        }
        try {
            for (Map.Entry<Thread,String> entry:ChatCoreWebSocketHandler.currentSessionId.entrySet()) {
                if (entry.getValue().equals(webSocketSessionDto.getSessionId())){
                    ChatCoreWebSocketHandler.currentSessionId.remove(entry.getKey());
                    break;
                }
            }
        }catch (Exception e){

        }
    }
}
