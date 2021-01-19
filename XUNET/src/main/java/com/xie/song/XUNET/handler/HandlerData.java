package com.xie.song.XUNET.handler;

import com.xie.song.XUNET.service.Datagram;

/**
 * 用于处理socketChannel接受到的数据
 */
public interface HandlerData extends Handler {

    /**
     *  用于写入数据的处理
     * @param chain 处理器链
     * @param datagram 数据报
     */
    void read(HandlerChain chain, Datagram datagram);

    /**
     *  用于输出数据的处理
     * @param chain 处理链
     * @param datagram 数据报
     */
    void write(HandlerChain chain, Datagram datagram);
}
