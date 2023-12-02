package com.imservices.im.bmm.websocket.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.annotation.Resource;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final static String uri = "/chat/socket";

    @Resource
    private ChatCoreWebSocketHandler chatCoreWebSocketHandler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //注册socket地址，以及允许所有请求  以及拦截器验证权限
        registry.addHandler(chatCoreWebSocketHandler, uri).addInterceptors(new HandshakeInterceptor()).setAllowedOrigins("*");
    }
}