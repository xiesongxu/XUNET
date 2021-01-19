package com.xie.song.XUNET.config;

/**
 * 用于包装客户端地址
 * 以及客户端要连接的地址
 */
public class ClientIpAddress {
    //客户端的IP地址
    private String address;

    //客户端的端口
    private int port;

    //客户端要连接的IP地址
    private String toAddress;

    //客户端要连接的端口
    private int toPort;


    /**
     * 四个构造器
     * @param address
     */
    public ClientIpAddress(String address) {
        this.address = address;
    }

    public ClientIpAddress(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public ClientIpAddress(String address, int port, String toAddress) {
        this.address = address;
        this.port = port;
        this.toAddress = toAddress;
    }

    public ClientIpAddress(String address, int port, String toAddress, int toPort) {
        this.address = address;
        this.port = port;
        this.toAddress = toAddress;
        this.toPort = toPort;
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

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public int getToPort() {
        return toPort;
    }

    public void setToPort(int toPort) {
        this.toPort = toPort;
    }
}
