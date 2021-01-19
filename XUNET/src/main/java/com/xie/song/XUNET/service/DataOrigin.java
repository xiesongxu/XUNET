package com.xie.song.XUNET.service;

import java.nio.channels.SocketChannel;

/**
 * 记录客户端的数据源
 * 如果是读事件，记录客户端发来的字节数组
 * 如果是写事件，记录客户端通道
 */
public class DataOrigin {

    //判断是否是读事件
    private boolean isRead;

    //客户端的传过来的数据
    private byte[] bytes;

    //客户端通道
    private SocketChannel socketChannel;

    //客户端发生的感兴趣事件
    private int readyOps;

    /**
     * 用于客户端读数据的构造器
     * @param isRead 是否是读事件
     * @param bytes 读取的字节数组
     */
    public DataOrigin(boolean isRead,byte[] bytes,SocketChannel socketChannel) {
        this.isRead = isRead;
        this.bytes = bytes;
        this.socketChannel = socketChannel;
    }

    /**
     * 用于客户都安写数据的构造器
     * @param isRead 是否是读事件
     * @param socketChannel 客户端通道
     * @param readyOps 客户端发生的感兴趣事件
     */
    public DataOrigin(boolean isRead,SocketChannel socketChannel,int readyOps) {
        this.isRead = isRead;
        this.socketChannel = socketChannel;
        this.readyOps = readyOps;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public int getReadyOps() {
        return readyOps;
    }

    public void setReadyOps(int readyOps) {
        this.readyOps = readyOps;
    }
}
