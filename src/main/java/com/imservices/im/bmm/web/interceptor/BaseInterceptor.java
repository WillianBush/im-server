package com.imservices.im.bmm.web.interceptor;

import com.imservices.im.bmm.annotation.HttpRequestDevice;
import com.imservices.im.bmm.utils.JsonUtil;
import com.imservices.im.bmm.utils.web.HttpRequestDeviceUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@SuppressWarnings("all")
public class BaseInterceptor extends HandlerInterceptorAdapter {
	private static final Log log = LogFactory.getLog(BaseInterceptor.class);  

	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		
		if(!StringUtils.isEmpty(request.getHeader("x-access-client"))) {
			String x_access_client = request.getHeader("x-access-client");
			request.getSession().setAttribute("x-access-client",x_access_client);
		}	
		
		//log.info("66666666666666666666666666666666666666666");
		////
		if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			return true;
		}
		
		
		
		HttpRequestDevice httpRequestDeviceAnnotaction = (HttpRequestDevice) ((HandlerMethod) handler)
				.getMethodAnnotation(HttpRequestDevice.class);
		if (httpRequestDeviceAnnotaction == null) {
			return true;
		}
		//如果配置为异步请求
		if(httpRequestDeviceAnnotaction.async()) {
			if(!HttpRequestDeviceUtils.isAsyncRequest(request)) {
				return false;
			}
		}
		if(httpRequestDeviceAnnotaction.device()==HttpRequestDevice.RequestDevice.all) {
			return true;
		} else if(httpRequestDeviceAnnotaction.device()==HttpRequestDevice.RequestDevice.mobile) {
			if(!HttpRequestDeviceUtils.isMobileDevice(request)) {
				return false;
			}
		} else if(httpRequestDeviceAnnotaction.device()==HttpRequestDevice.RequestDevice.pc) {
			if(HttpRequestDeviceUtils.isMobileDevice(request)||HttpRequestDeviceUtils.isWeixin(request)) {
				return false;
			}
		} else if(httpRequestDeviceAnnotaction.device()==HttpRequestDevice.RequestDevice.weixin) {
			if(!HttpRequestDeviceUtils.isWeixin(request)) {
				return false;
			}
		} 
		return true;
	}
	

	private void writeAuthorizeFailResponse(HttpServletResponse response,String msg)
			throws Exception {
		String json = JsonUtil.getJSONString((Map) ImmutableMap.of("code",
				Integer.valueOf(401), "msg", msg));
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	

}

