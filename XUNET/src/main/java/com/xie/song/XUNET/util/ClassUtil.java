package com.xie.song.XUNET.util;

import com.xie.song.XUNET.exception.ExistException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类类型的工具类
 */
public class ClassUtil {

    /**
     * 给定目标的类型，然后通过指定类型来创建对象
     * @param targetClass 给定目标的类类型
     * @return
     */
    public static Object createObjectByClass(Class targetClass) {
        Constructor constructor = null;
        try {
            constructor = targetClass.getConstructor(null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Object newInstance = null;
        try {
            newInstance = constructor.newInstance(null);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return newInstance;
    }

    /**
     * 通过字符串数组里的类参考路径，通过反射加载类字节码文件进java虚拟机
     * 字符串数组里的每个元素是独立的类参考路径
     * @param s 输入存有类参考路径的字符串数组
     * @return
     */
    public static Object[] createObjectsByStrings(String[] s) {
        List list = new ArrayList();
        for(String target : s) {
            try {
                Class<?> aClass = Class.forName(target);
                Object object = createObjectByClass(aClass);
                list.add(object);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list.toArray();
    }

}
