package com.xie.song.XUNET.service;

import java.nio.channels.SocketChannel;

/**
 * 记录数据处理过程的数据报
 */
public class Datagram {

    //记录是否是读取数据
    private boolean isRead;

    //数据报的字节数组
    private byte[] bData;

    //数据报的字符串
    private String sData;

    private SocketChannel socketChannel;

    /**
     *
     * @param isRead
     * @param bData 字节数组
     */
    public Datagram(boolean isRead,byte[] bData,SocketChannel socketChannel) {
        this.isRead = isRead;
        this.bData = bData;
        this.socketChannel = socketChannel;
    }

    /**
     *
     * @param isRead
     * @param sData 字符串
     */
    public Datagram(boolean isRead,String sData) {
        this.isRead = isRead;
        this.sData = sData;
    }

    public Datagram(boolean isRead,SocketChannel socketChannel) {
        this.isRead = isRead;
        this.socketChannel = socketChannel;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public byte[] getbData() {
        return bData;
    }

    public void setbData(byte[] bData) {
        this.bData = bData;
    }

    public String getsData() {
        return sData;
    }

    public void setsData(String sData) {
        this.sData = sData;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
