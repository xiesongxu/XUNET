package com.xie.song.XUNET.service;

/**
 * 用于接受客户端连接 或 客户端发来的数据
 */
public interface Acceptor<T> {

    /**
     * 获取客户端 或 客户端的发来的数据
     * @return
     */
    T getTarget();
}
