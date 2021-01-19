package com.xie.song.XUNET.service;

import com.xie.song.XUNET.config.Configuration;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 通道接收器，用于提交到通道任务队列中
 * 然后执行返回连接到的客户端通道
 */
public class ChannelAcceptor implements Acceptor<SocketChannel>,Comparable {

    //服务端通道
    private ServerSocketChannel serverSocketChannel;

    //服务端发生的感兴趣事件
    private volatile int readyOps;

    private Configuration configuration;

    private volatile SocketChannel socketChannel = null;

    /**
     *
     * @param serverSocketChannel 服务端通道
     * @param readyOps 服务端发生的感兴趣事件
     * @param configuration 配置类
     */
    public ChannelAcceptor(ServerSocketChannel serverSocketChannel,int readyOps,Configuration configuration) {
        this.serverSocketChannel = serverSocketChannel;
        this.readyOps = readyOps;
        this.configuration = configuration;
        acceptorChannel();
    }


    public void acceptorChannel() {
        if ((readyOps & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
            try {
                socketChannel = serverSocketChannel.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取来连接的客户端通道
     * @return
     */
    @Override
    public SocketChannel getTarget() {
        return socketChannel;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
