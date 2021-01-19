package com.xie.song.XUNET.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于服务端地址配置
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerAddress {

    /**
     * 配置服务端的IP地址
     * @return
     */
    String address() default "127.0.0.1";

    /**
     * 配置服务端的端口
     * @return
     */
    int port() default 8888;
}
