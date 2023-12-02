package com.imservices.im.bmm.web.config;

import com.imservices.im.bmm.bean.store.ChatStoreComponent;
import com.imservices.im.bmm.service.EmployeeService;
import com.imservices.im.bmm.service.IpListService;
import com.imservices.im.bmm.service.MemberService;
import com.imservices.im.bmm.web.interceptor.BaseInterceptor;
import com.imservices.im.bmm.web.interceptor.MemberInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web配置
 * @author wang<fangyuan.co@outlook.com>
 */
//@DependsOn("springContextUtils")
@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer, ErrorPageRegistrar {

    private MemberService memberService;

    private ChatStoreComponent chatStoreComponent;


    private EmployeeService employeeService;


    private IpListService ipListService;
    /**
     * 配置拦截器
     * @param registry
     */
    @Override

    public void addInterceptors(InterceptorRegistry registry) {
        // 注册rest拦截器
        registry.addInterceptor(new BaseInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new MemberInterceptor(memberService,chatStoreComponent,employeeService,ipListService)).addPathPatterns("/user/**","/red/**","/agent/**","/room/**","/mdr/**");
    }


    /**
     * 错误页面
     * @param registry
     */
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage notFound = new ErrorPage(HttpStatus.NOT_FOUND, "/error/404.html");
        ErrorPage sysError = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500.html");
        registry.addErrorPages(notFound, sysError);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("ORIGINS").maxAge(3600);
        WebMvcConfigurer.super.addCorsMappings(registry);
    }

}
