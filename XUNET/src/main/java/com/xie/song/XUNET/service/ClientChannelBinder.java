package com.xie.song.XUNET.service;

import com.xie.song.XUNET.config.ClientIpAddress;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Comparator;

/**
 * 返回构造好的客户端通道
 * 然后注册到选择器中
 */
public class ClientChannelBinder implements Binder<SocketChannel>, Comparable {

    private String address;

    private int port;

    //连接的IP地址
    private String toAddress;

    //连接的端口
    private int toPort;

    private int priority;

    private ClientIpAddress clientIpAddress;

    /**
     *
     * @param clientIpAddress 客户端地址
     */
    public ClientChannelBinder(ClientIpAddress clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
        this.address = clientIpAddress.getAddress();
        this.port = clientIpAddress.getPort();
        this.toAddress = clientIpAddress.getToAddress();
        this.toPort = clientIpAddress.getToPort();
    }

    /**
     *
     * @param clientIpAddress 客户端地址
     * @param priority 优先级
     */
    public ClientChannelBinder(ClientIpAddress clientIpAddress,int priority) {
        this.clientIpAddress = clientIpAddress;
        this.address = clientIpAddress.getAddress();
        this.port = clientIpAddress.getPort();
        this.toAddress = clientIpAddress.getToAddress();
        this.toPort = clientIpAddress.getToPort();
        this.priority = priority;
    }

    /**
     * 构造客户端通道
     * @return
     */
    public SocketChannel getChannel() {
        SocketChannel socket = null;
        try {
            socket = SocketChannel.open();
            socket.socket().bind(new InetSocketAddress(address,port));
            socket.connect(new InetSocketAddress(toAddress, toPort));
            socket.isConnectionPending();
            socket.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    /**
     * 在优先级队列中比较
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        ClientChannelBinder binder = (ClientChannelBinder)o;
        return this.priority - binder.priority;
    }
}
