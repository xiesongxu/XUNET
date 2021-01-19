package com.xie.song.XUNET.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加上了@Bean注解的方法，用于为配置框架提供组件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Bean {

    /**
     *
     * @return 用于标注这个bean叫什么名字
     */
    String beanName() default "" ;

}
