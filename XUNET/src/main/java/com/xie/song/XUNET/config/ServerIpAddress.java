package com.xie.song.XUNET.config;

/**
 * 用于包装服务端的地址
 */
public class ServerIpAddress {

    //服务端的地址
    private String address;

    //服务端的端口
    private int port;

    /**
     * 两个构造器
     * @param address
     */
    public ServerIpAddress(String address) {
        this.address = address;
    }

    public ServerIpAddress(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
