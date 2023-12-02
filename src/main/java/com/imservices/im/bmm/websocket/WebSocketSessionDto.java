package com.imservices.im.bmm.websocket;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

@Data
@Slf4j
public class WebSocketSessionDto {
    private String sessionId;
    private String appUUid;

    public WebSocketSessionDto(WebSocketSession websocketSession) {
        String app_uuid = websocketSession.toString().split("app_uuid=")[1].replaceAll("]", "");
        if (app_uuid.endsWith("#")){
            app_uuid = app_uuid.replace("#","");
        }
        if (app_uuid.endsWith("#/")){
            app_uuid = app_uuid.replace("#/","");
        }
        if (app_uuid.contains("session_id=")){
            String[] sessionWithAppUid = app_uuid.split("&session_id=");
            app_uuid = sessionWithAppUid[0];
            if (sessionWithAppUid.length >1) {
                this.sessionId = sessionWithAppUid[1];
//                log.info("app_uuid:{};sessionId:{}",app_uuid,sessionId);
            }
        }
         this.appUUid = app_uuid;
    }
}
