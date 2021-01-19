package com.xie.song.XUNET.handler;

import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.service.Datagram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 管理HandlerData的链
 */
public class DataHandlerChain implements HandlerChain<Datagram> {

    List<HandlerData> dataChain = new ArrayList<HandlerData>();

    private Configuration configuration;

    //存储数据处理器的下标
    private HashMap<HandlerData,Integer> handlerDataIndex = new HashMap();

    /**
     * 记录每个线程应该调用哪个处理器的下标
     */
    private ThreadLocal<Integer> threadLocal = new ThreadLocal();

    //记录数据链的长度
    private int dataChainLength;

    public DataHandlerChain() {

    }

    public DataHandlerChain(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 初始化handlerDataIndex集合
     */
    @Override
    public void init() {
        int size = dataChain.size();
        for (int i = 0; i < size; i++) {
            HandlerData handlerData = dataChain.get(i);
            handlerDataIndex.put(handlerData,i);
        }
        dataChainLength = dataChain.size();
    }

    /**
     * 写入数据的管理
     * @param data
     */
    @Override
    public void proceed(Datagram data) {

    }

    /**
     * 输出数据的管理
     * @param datagram 数据报
     * @param handlerData 已经调用过的数据处理器
     */
    @Override
    public void write(Datagram datagram, HandlerData handlerData) {
        Integer dataHandlerIndex = 0;
        if (handlerData == null) {
            dataHandlerIndex = dataChainLength - 1;
        } else {
            dataHandlerIndex = handlerDataIndex.get(handlerData) - 1;
        }
        if (dataHandlerIndex.intValue() < 0) {
            //获取执行次数
            byte[] bytes = datagram.getbData();
            if (bytes == null || bytes.length == 0) {
                return;
            }
            SocketChannel socketChannel = datagram.getSocketChannel();
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            try {
                socketChannel.write(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            HandlerData targetHandlerData = dataChain.get(dataHandlerIndex);
            targetHandlerData.write(this,datagram);
        }
    }


    /**
     * 客户端通道输入数据的管理
     * @param datagram 数据报
     * @param handlerData 已经调用过的数据处理器
     */
    @Override
    public void read(Datagram datagram, HandlerData handlerData) {
        int dataHandlerIndex = 0;
        if (handlerData == null) {
            dataHandlerIndex = 0;
        } else {
            dataHandlerIndex = handlerDataIndex.get(handlerData) + 1;
        }
        if (dataHandlerIndex == dataChain.size()) {
            return;
        } else {
            HandlerData targetHandlerData = dataChain.get(dataHandlerIndex);
            targetHandlerData.read(this,datagram);
        }
    }

    /**
     * 判断是否为null
     * @return
     */
    public boolean isEmpty() {
        return dataChain.isEmpty();
    }

    public List getHandlerChain() {
        return dataChain;
    }


}
