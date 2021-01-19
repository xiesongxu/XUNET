package com.xie.song.XUNET.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {

    /**
     * true：框架将会初始化为服务端，
     * false：框架将会初始化为客户端
     * @return
     */
    boolean isServer() default true;



    /**
     * 配置客户端的地址
     * @return
     */
    ClientAddress[] clientAddress() default @ClientAddress;

    /**
     * 配置服务端的地址
     * @return
     */
    ServerAddress[] serverAddress() default @ServerAddress;


}
