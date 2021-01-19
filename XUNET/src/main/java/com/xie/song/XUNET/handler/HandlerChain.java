package com.xie.song.XUNET.handler;

import com.xie.song.XUNET.service.Datagram;

import java.util.List;

/**
 * 处理器链，用于管理处理器
 */
public interface HandlerChain<T> {

    /**
     * 用于调用下一个处理器
     */
    public void proceed(T t);

    /**
     * 根据数据处理的下标来判断应该调用哪个处理器
     * @param t 数据报
     * @param handlerData 已经调用过的数据处理器
     */
    void write(T t,HandlerData handlerData);

    /**
     * 客户端通道输入数据的管理
     * @param t 数据报
     * @param handlerData 已经调用过的数据处理器
     */
    void read(T t,HandlerData handlerData);

    /**
     *判断处理器链是否为null
     * @return
     */
    boolean isEmpty();

    /**
     * 获取处理器链
     * @return
     */
    List getHandlerChain();

    /**
     * 初始化处理器链
     */
    void init();


}
