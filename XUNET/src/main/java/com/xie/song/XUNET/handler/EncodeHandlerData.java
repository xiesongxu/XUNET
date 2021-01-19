package com.xie.song.XUNET.handler;

import com.xie.song.XUNET.service.Datagram;

/**
 * 对写出的数据进行编码操作
 */
public class EncodeHandlerData implements HandlerData {
    /**
     * 空方法
     * @param chain 处理器链
     * @param datagram 数据报
     */
    @Override
    public void read(HandlerChain chain, Datagram datagram) {
        chain.read(datagram,this);
    }

    /**
     * 把字符串转换为字节数组
     * @param chain 处理链
     * @param datagram 数据报
     */
    @Override
    public void write(HandlerChain chain, Datagram datagram) {
        String s = datagram.getsData();
        if (!s.isEmpty() || !s.equals("")) {
            datagram.setbData(s.getBytes());
        }
        chain.write(datagram,this);
    }
}
