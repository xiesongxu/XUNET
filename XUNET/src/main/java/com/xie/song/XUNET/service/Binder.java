package com.xie.song.XUNET.service;

/**
 * 用于给选择器绑定通道
 * @param <T> 泛型用于确定获取的是服务通道还是客户通道
 */
public interface Binder<T> {

    /**
     * 获取通道
     * @return
     */
    public T getChannel();

}
