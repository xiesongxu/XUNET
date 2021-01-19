package com.xie.song.XUNET.service;

import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.handler.HandlerChain;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 用于处理服务端接受连接的任务
 */
public class ReactorTask implements Runnable {

    private Configuration configuration;

    private Thread singleThread = null;

    //选择器
    private Selector selector;

    //保存提交的任务
    private BlockingQueue queue = new PriorityBlockingQueue<Binder>();

    public ReactorTask(Configuration configuration) {
        this.configuration = configuration;
        init();
    }

    /**
     * 初始化选择器，和通道处理器链
     */
    public void init() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单线程运行的任务
     * 用于处理任务队列中的任务
     * 和selector选择器发生的感兴趣事件
     */
    public void run() {
        if (singleThread == null) {
            singleThread = Thread.currentThread();
        }
        //空轮询次数判断
        int emptyPollIndex = 0;

        //循环处理任务
        while(true) {
            try {
                //处理任务队列,注册Channel通道进Selector选择器中
                if (!queue.isEmpty()) {
                    for(int i = 0; i < queue.size(); i++) {
                        Binder binder = (Binder) queue.take();
                        ServerSocketChannel serverChannel = (ServerSocketChannel) binder.getChannel();
                        if (serverChannel != null) {
                            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                        }
                    }
                }
            } catch (InterruptedException | ClosedChannelException e) {
                e.printStackTrace();
            }
            int select = 0;
            try {
                select = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //处理发生的空轮询
            if (select == 0) {
                emptyPollIndex++;
                if(emptyPollIndex == 30) {
                    Set<SelectionKey> selectionKeys = selector.keys();
                    Selector newSelector = null;
                    try {
                        newSelector = Selector.open();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for(SelectionKey key : selectionKeys) {
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
                        ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                        int readyOps = key.readyOps();
                        ChannelAcceptor channelAcceptor = new ChannelAcceptor(channel,readyOps,configuration);
                        configuration.doChannelAcceptorToTaskQueue(channelAcceptor);
                    }
                }
                //空轮询数置为0
                emptyPollIndex = 0;
            }
        }
    }

    /**
     * 给任务队列添加任务
     * @param o
     */
    public void addTask(Object o) {
        try {
            queue.put(o);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取选择器
     * @return
     */
    public Selector getSelector() {
        return selector;
    }

}
