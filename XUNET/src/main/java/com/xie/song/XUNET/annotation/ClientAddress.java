package com.xie.song.XUNET.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于客户端地址配置
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClientAddress {

    /**
     * 配置客户端的IP地址
     */
    String address() default "127.0.0.1";

    /**
     * 配置客户端的端口
     * @return
     */
    int port() default 8888;

    /**
     * 配置客户端要连接的IP地址
     * @return
     */
    String toAddress() default "";

    /**
     * 配置客户端要连接的端口
     * @return
     */
    int toPort() default 0;

}
