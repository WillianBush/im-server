package com.imservices.im.bmm.utils.web;

import com.imservices.im.bmm.utils.JsonUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@SuppressWarnings("all")
public class ResponseUtils {

	public enum STATUS {
		success,error
	}
	
	//如果msg为空则前台不显示信息，如果redirectUri为空前台不做跳转
	public static void json( HttpServletResponse response,STATUS status,String msg,String redirectUri) {
		try {
			String json = JsonUtil.getJSONString((Map) ImmutableMap.of("status",status.name(), "msg", StringUtils.isEmpty(msg)?"":msg,"redirectUri",StringUtils.isEmpty(redirectUri)?"":redirectUri));
			if (null == redirectUri ){
				response.setContentType("application/json");
			}else {
				response.setContentType("text/html");
			}
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}catch (Exception e) {
			log.error("json response",e);
		}
	}
	
	//如果msg为空则前台不显示信息，如果redirectUri为空前台不做跳转
	public static void json( HttpServletResponse response,int code,String msg,String redirectUri)  {
		try {
			String json = JsonUtil.getJSONString((Map) ImmutableMap.of("code",code, "msg", StringUtils.isEmpty(msg)?"":msg,"redirectUri",StringUtils.isEmpty(redirectUri)?"":redirectUri));
			if (null == redirectUri ){
				response.setContentType("application/json");
			}else {
				response.setContentType("text/html");
			}
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}catch (Exception e) {
			log.error("response json",e);
		}

	}


	public static void json(HttpServletResponse response, int code, Object body, String redirectUri)  {
		try {
			String json = JsonUtil.getJSONString((Map) ImmutableMap.of("code",code, "body", body,"redirectUri",StringUtils.isEmpty(redirectUri)?"":redirectUri));
			if (null == redirectUri ){
				response.setContentType("application/json");
			}else {
				response.setContentType("text/html");
			}
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		}catch (Exception e) {
			log.error("json response",e);
		}

	}
	
}
