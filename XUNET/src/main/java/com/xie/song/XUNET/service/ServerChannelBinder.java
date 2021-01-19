package com.xie.song.XUNET.service;


import com.xie.song.XUNET.config.ServerIpAddress;

import java.io.IOException;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Comparator;
import java.util.Set;

/**
 * 返回构造好的服务端通道，
 * 然后把此对象提交到任务队列中，
 * 执行任务把服务端通道注册到选择器中
 */
public class ServerChannelBinder implements Binder<ServerSocketChannel>, Comparable {

    //提供的服务端地址
    private String address;

    //提供的服务端端口
    private int port;

    private ServerIpAddress serverIpAddress;

    private int priority = 0;

    /**
     * @param serverIpAddress 提供服务端地址
     */
    public ServerChannelBinder(ServerIpAddress serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
        this.address = serverIpAddress.getAddress();
        this.port = serverIpAddress.getPort();
    }

    /**
     * @param serverIpAddress 提供服务端地址
     * @param priority 和优先级
     */
    public ServerChannelBinder(ServerIpAddress serverIpAddress,int priority) {
        this.priority = priority;
        this.serverIpAddress = serverIpAddress;
        this.address = serverIpAddress.getAddress();
        this.port = serverIpAddress.getPort();
    }

    /**
     * 构造服务端通道
     * @return
     */
    public ServerSocketChannel getChannel() {
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(address,port));
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverSocketChannel;
    }

    /**
     * 在任务队列里的优先级
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        ServerChannelBinder binder = (ServerChannelBinder)o;
        return this.priority - binder.priority;
    }
}
