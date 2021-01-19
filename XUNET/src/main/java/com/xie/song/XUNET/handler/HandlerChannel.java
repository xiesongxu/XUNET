package com.xie.song.XUNET.handler;

import java.nio.channels.SocketChannel;

/**
 * 用于处理serverSocketChannel接受到的channel
 */
public interface HandlerChannel extends Handler {

    /**
     *
     * @param chain 处理器链
     * @param socketChannel 客户端通道
     */
    void handler(HandlerChain chain, SocketChannel socketChannel);

}
