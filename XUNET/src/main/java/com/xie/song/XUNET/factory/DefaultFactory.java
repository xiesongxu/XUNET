package com.xie.song.XUNET.factory;

import com.xie.song.XUNET.annotation.Bean;
import com.xie.song.XUNET.exception.ExistException;
import com.xie.song.XUNET.util.AnnotationUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认提供的工厂
 */
public class DefaultFactory extends AbstractFactory {

    private final ConcurrentHashMap<Class, List<String>> factoryCache = new ConcurrentHashMap();

    /**
     * 通过指定方法来创建对应的bean
     * @param method 用于注入bean的方法，方法上必须带@bean注解
     * @param config 配置类对象
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public void doCreateBeanByMethod(Method method,Object config) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Class targetClass = method.getReturnType();
        Bean bean = method.getAnnotation(Bean.class);
        String beanName = bean.beanName();
        if(beanName == null || beanName.equals("")){
            String sName = method.getReturnType().getSimpleName();
            Object target = method.invoke(config, null);
            storeFactoryCache(targetClass,sName);
            try {
                put(sName, target);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Object target = method.invoke(config,null);
            storeFactoryCache(targetClass,beanName);
            try {
                put(beanName, target);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 把目标类型和beanName放入工厂缓存
     * @param targetClass 目标类型
     * @param beanName
     */
    public void storeFactoryCache(Class targetClass,String beanName) {
        if(factoryCache.contains(targetClass)){
            List list = factoryCache.get(targetClass);
            list.add(beanName);
        } else {
            List<String> list = new ArrayList();
            list.add(beanName);
            factoryCache.put(targetClass,list);
        }
    }

    /**
     * 通过指定类型来获取对应的bean数组
     * 可以获取指定类型的子类类型或相同类型，
     * 如果没有匹配的返回null
     * @param targetClass 目标类型
     * @return
     * @throws ExistException
     */
    public Object[] getBeansByType(Class targetClass) {
        List returnObjects = new ArrayList<Object>();
        Enumeration<Class> keys = factoryCache.keys();
        while(keys.hasMoreElements()){
            Class aClass = keys.nextElement();
            if(targetClass.isAssignableFrom(aClass) || targetClass.equals(aClass)){
                List list = factoryCache.get(aClass);
                Object[] objects = list.toArray();
                for(Object o : objects){
                    Object bean = getBean((String) o);
                    returnObjects.add(bean);
                }
            }
        }

        if(returnObjects.isEmpty()){
            return null;
        }

        return returnObjects.toArray();
    }



}
