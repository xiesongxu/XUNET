package com.xie.song.XUNET.factory;

import com.xie.song.XUNET.exception.ExistException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 提供工厂的模板
 */
public abstract class AbstractFactory implements Factory {


    private  final ConcurrentHashMap<String,Object> singletonBeans = new ConcurrentHashMap();

    private  final ReentrantReadWriteLock writeReadLock = new ReentrantReadWriteLock();

    //读锁
    private  final ReentrantReadWriteLock.ReadLock readLock = writeReadLock.readLock();

    //写锁
    private  final ReentrantReadWriteLock.WriteLock writeLock = writeReadLock.writeLock();

    /**
     * 通过beanName来获取bean
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        readLock.lock();
        try{
            boolean contains = singletonBeans.contains(beanName);
            if (contains) {
                return singletonBeans.get(beanName);
            }
            return null;
        } finally {
            readLock.unlock();
        }

    }

    /**
     * 通过beanType来获取指定bean
     * beanName为类名首字母小写
     * @param beanType
     * @return
     */
    public Object getBean(Class beanType) {
        readLock.lock();
        try{
            String simpleName = beanType.getSimpleName();
            String beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
            boolean contains = singletonBeans.contains(beanName);
            if (contains) {
                return singletonBeans.get(beanName);
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 放入指定的beanName 和 bean
     * @param beanName
     * @param bean
     * @throws ExistException
     */
    public void put(String beanName, Object bean) throws ExistException {
        writeLock.lock();
        try{
            singletonBeans.put(beanName,bean);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 放入bean，beanName为类名首字母小写
     * @param bean
     * @throws ExistException
     */
    public void put(Object bean) throws ExistException {
        writeLock.lock();
        try{
            String simpleName = bean.getClass().getSimpleName();
            String beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
            singletonBeans.put(beanName,bean);
        } finally {
            writeLock.unlock();
        }
    }

    public abstract void doCreateBeanByMethod(Method method, Object target) throws IllegalAccessException, InstantiationException, InvocationTargetException;

    public abstract Object[] getBeansByType(Class targetClass) ;

}
