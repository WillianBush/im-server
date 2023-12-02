package com.imservices.im.bmm.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminAuthPassport
{
  boolean value() default true;//是否参与验证 。默认true参与验证
  boolean json() default false;//验证结果是否返回json格式【针对异步请求的情况】
}