package com.imservices.im.bmm.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//处理允许的请求条件
@Inherited
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRequestDevice {

	//请求设备
	public enum RequestDevice {
		all,pc,mobile,weixin
	}
	
	RequestDevice device() default RequestDevice.all;
	boolean async() default false;//异步请求
}
