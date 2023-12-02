package com.imservices.im.bmm.websocket.core;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

	public static final String HTTP_SESSION_ATTR_NAME = "HTTP.SESSION";

	public boolean beforeHandshake(ServerHttpRequest request,ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		if (request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
			request.getHeaders().set("Sec-WebSocket-Extensions","permessage-deflate");
		}
		HttpSession session = getSession(request);
		if (session != null) {
			attributes.put(HTTP_SESSION_ATTR_NAME, session);
		}
		return super.beforeHandshake(request, response, wsHandler, attributes);
	}

	public void afterHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,Exception ex) {
		super.afterHandshake(request, response, wsHandler, ex);
	}

	private HttpSession getSession(ServerHttpRequest request) {
		if ((request instanceof ServletServerHttpRequest)) {
			ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
			return serverRequest.getServletRequest().getSession(isCreateSession());
		}
		return null;
	}
}
