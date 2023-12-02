package com.imservices.im.bmm.common;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 跨域配置
 */
@WebFilter(filterName = "CorsFilter", urlPatterns = "/*")
@Slf4j
public class CORSFilter implements Filter {
 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
 
    }
 
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    	 log.info("CORSFilter");
        HttpServletResponse response = (HttpServletResponse) servletResponse;
         HttpServletRequest request = (HttpServletRequest)servletRequest;
  
         response.setHeader("Access-Control-Allow-Origin", "*");
         response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
         response.setHeader("Access-Control-Max-Age", "3600");
         response.setHeader("Access-Control-Allow-Headers", "Content-Type,XFILENAME,XFILECATEGORY,XFILESIZE,x-requested-with,Authorization,x-access-uid,x-access-roomid,x-access-client");
         response.setHeader("Access-Control-Allow-Credentials", "true");
         String method = request.getMethod();
         if(method.equalsIgnoreCase("OPTIONS")){
//             servletResponse.getOutputStream().write("Success".getBytes(StandardCharsets.UTF_8));
             response.setStatus(HttpServletResponse.SC_OK);
         }else{
             filterChain.doFilter(servletRequest, servletResponse);
         }
    }
 
    @Override
    public void destroy() {
 
    }
}