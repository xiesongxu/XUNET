package com.xie.song.XUNET.service;

import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.handler.HandlerChain;

import java.io.IOException;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class WorkerTask implements Runnable {

    private Configuration configuration;

    //记录运行当前任务的线程
    private Thread singleThread;

    //任务中的选择器
    private Selector selector;

    //记录处理链
    private HandlerChain handlerChain;

    //保存提交的任务
    private BlockingQueue queue = new PriorityBlockingQueue<Binder>();

    //记录读写操作
    private static final int OP_WRITE = SelectionKey.OP_WRITE;

    private static final int OP_READ = SelectionKey.OP_READ;

    //判断客户端是读数据还是写数据
    private boolean isRead;


    /**
     *
     * @param configuration 配置类
     * @param isRead 是否是读事件
     */
    public WorkerTask(Configuration configuration,boolean isRead) {
        this.configuration = configuration;
        this.isRead = isRead;
        init();
    }

    /**
     * 初始化选择器，和数据处理器链
     */
    public void init() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.handlerChain = configuration.getDataHandlerChain();

    }

    /**
     * 主要逻辑
     */
    public void run() {
        if (singleThread == null) {
            singleThread = Thread.currentThread();
        }
        //空轮询下标
        int emptyPollIndex = 0;
        while (true) {
            //任务队列不为null，处理任务队列
            if (!queue.isEmpty()) {
                Binder take = null;
                try {
                      take = (Binder) queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (take instanceof ClientChannelBinder) {
                    ClientChannelBinder clientChannelBinder = (ClientChannelBinder) take;
                    SocketChannel channel = clientChannelBinder.getChannel();
                    if (isRead) {
                        try {
                            channel.register(selector,OP_READ);
                        } catch (ClosedChannelException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            channel.register(selector,OP_WRITE);
                        } catch (ClosedChannelException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    SocketChannel channel = (SocketChannel) take.getChannel();
                    try {
                        channel.register(selector,SelectionKey.OP_READ);
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();
                    }
                }
            }
            int select = 0;
            try {
                //等待接受感兴趣事件
                 select = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (select == 0) {
                //发生空轮询事件，进行处理
                emptyPollIndex++;
                if (emptyPollIndex == 30) {
                    Set<SelectionKey> keys = selector.keys();
                    Selector newSelector = null;
                    try {
                        newSelector = Selector.open();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (SelectionKey key : keys) {
                        try {
                            key.channel().register(newSelector,key.interestOps());
                        } catch (ClosedChannelException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        selector.close();
                        selector = newSelector;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //处理发生的感兴趣事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    if (key.isValid()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        int readyOps = key.readyOps();
                        DataAcceptor dataAcceptor = new DataAcceptor(channel, readyOps);
                        //把数据接收器注册到任务队列中
                        configuration.doDataAcceptorToTaskQueue(dataAcceptor);
                        if ((readyOps & OP_WRITE) == OP_WRITE) {
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                }
                //空轮询数置为0
                emptyPollIndex = 0;
            }
        }
    }

    /**
     * 添加任务
     * @param o
     */
    public void addTask(Object o) {
        try {
            queue.put(o);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public Selector getSelector() {
        return selector;
    }

}
