package com.xie.song.XUNET.service;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * 用于绑定接受到的客户端通道
 * 绑定到指定的任务队列
 */
public class AcceptChannelBinder implements Binder,Comparable {

    private SocketChannel socketChannel;

    public AcceptChannelBinder(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * @return 返回客户端通道对象
     */
    @Override
    public Object getChannel() {
        try {
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socketChannel;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
