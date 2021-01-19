package com.xie.song.XUNET.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 用于接受客户端的信息，
 * 如果客户端是写数据将会交给数据任务进行处理
 */
public class DataAcceptor implements Acceptor<DataOrigin>,Comparable {

    private SocketChannel socketChannel;

    //发生的感兴趣事件
    private volatile int readyOps;

    //分配缓存，大小为1024
    private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    //记录数据源
    private volatile DataOrigin dataOrigin;

    public DataAcceptor(SocketChannel socketChannel,int readyOps) {
        this.socketChannel =socketChannel;
        this.readyOps = readyOps;
        acceptorData();
    }

    /**
     * 接受数据
     */
    public void acceptorData() {
        if((readyOps & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
            try {
                //读取数据到缓存里
                socketChannel.read(byteBuffer);
                byteBuffer.flip();
                int limit = byteBuffer.limit();
                byte[] bytes = new byte[limit];
                byteBuffer.get(bytes);
                //创建一个数据源
                dataOrigin = new DataOrigin(true, bytes,socketChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ((readyOps & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
            //创建一个数据源
            dataOrigin = new DataOrigin(false,socketChannel,readyOps);
        } else {
            dataOrigin = null;
        }
    }


    /**
     * 返回指定的数据源
     * @return
     */
    @Override
    public DataOrigin getTarget() {
        return dataOrigin;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
