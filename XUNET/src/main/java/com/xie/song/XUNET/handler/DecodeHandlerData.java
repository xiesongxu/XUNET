package com.xie.song.XUNET.handler;

import com.xie.song.XUNET.service.Datagram;

/**
 * 用于给客户端通道传入的数据进行解码操作
 */
public class DecodeHandlerData implements HandlerData {

    /**
     * 对客户端通道传入的字节数组进行解码操作
     * @param chain 处理器链
     * @param datagram 数据报
     */
    @Override
    public void read(HandlerChain chain, Datagram datagram) {
        byte[] bytes = datagram.getbData();
        if (bytes.length != 0 || bytes != null) {
            datagram.setsData(new String(bytes));
        }
        chain.read(datagram,this);
    }

    /**
     * 空方法
     * @param chain 处理链
     * @param datagram 数据报
     */
    @Override
    public void write(HandlerChain chain, Datagram datagram) {
        chain.write(datagram,this);
    }
}
