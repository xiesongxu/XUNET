package com.xie.song.XUNET.factory;

import com.xie.song.XUNET.exception.ExistException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 维护一个单例池
 */
public interface Factory {

    //通过beanName来获取bean
    Object getBean(String beanName);

    //通过beanType来获取bean
    Object getBean(Class beanType);

    //放入bean
    void put(String beanName,Object bean) throws ExistException;

    //放入bean，beanName为类名的首字母小写
    void put(Object bean) throws ExistException;

    void doCreateBeanByMethod(Method method, Object target) throws IllegalAccessException, InstantiationException, InvocationTargetException;

    Object[] getBeansByType(Class targetClass) ;
}
