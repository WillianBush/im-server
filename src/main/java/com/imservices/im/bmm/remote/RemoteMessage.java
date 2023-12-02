package com.imservices.im.bmm.remote;

import com.imservices.im.bmm.websocket.Message;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
public class RemoteMessage {
   private String toUid;
   private Message message;
   private String CMD;
}
