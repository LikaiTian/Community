package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解 @LoginRequired
 */

@Target(ElementType.METHOD) //表示该注解作用在方法上
@Retention(RetentionPolicy.RUNTIME) //表示该注解在运行时起作用
public @interface LoginRequired {}
