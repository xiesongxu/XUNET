package com.xie.song.XUNET.handler;

import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.service.AcceptChannelBinder;
import com.xie.song.XUNET.service.Datagram;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于管理HandlerChannel的链
 */
public class ChannelHandlerChain implements HandlerChain<SocketChannel> {

    List<HandlerChannel> channelChain = new ArrayList<HandlerChannel>();

    private ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>();

    private Configuration configuration;

    public ChannelHandlerChain() {

    }

    /**
     * @param configuration 配置类
     */
    public ChannelHandlerChain(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 遍历通道处理器处理客户端通道
     * @param socketChannel
     */
    public void proceed(SocketChannel socketChannel) {
        int channelChainIndex = addIndexToThread();
        if (channelChainIndex == channelChain.size()) {
            AcceptChannelBinder channelBinder = new AcceptChannelBinder(socketChannel);
            configuration.doClientBinderToTaskQueue(channelBinder);
            removeIndexFromThread();
        } else {
            HandlerChannel handlerChannel = channelChain.get(channelChainIndex);
            handlerChannel.handler(this,socketChannel);
        }
    }


    /**
     * 获取通道处理链的下标，把下标存储在当前线程中
     * @return
     */
    public int addIndexToThread() {
        Integer index = threadLocal.get();
        if (index == null) {
            threadLocal.set(0);
        } else {
            threadLocal.set(threadLocal.get() + 1);
        }
        return threadLocal.get();
    }

    /**
     * 移除当前线程中的通道处理链下标
     */
    public void removeIndexFromThread() {
        threadLocal.remove();
    }

    /**
     * 空方法
     * @param socketChannel
     * @param handlerData 已经调用过的数据处理器
     */
    @Override
    public void write(SocketChannel socketChannel, HandlerData handlerData) {

    }

    /**
     * 空方法
     * @param socketChannel
     * @param handlerData 已经调用过的数据处理器
     */
    @Override
    public void read(SocketChannel socketChannel, HandlerData handlerData) {

    }

    /**
     * 判断处理链是否为null
     * @return
     */
    public boolean isEmpty() {
        return channelChain.isEmpty();
    }

    public List getHandlerChain() {
        return channelChain;
    }

    @Override
    public void init() {

    }
}
