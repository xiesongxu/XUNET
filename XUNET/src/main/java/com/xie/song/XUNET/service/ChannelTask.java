package com.xie.song.XUNET.service;

import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.handler.HandlerChain;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 把通道处理的过程包装成一个任务
 */
public class ChannelTask implements Runnable {

    private Configuration configuration;

    private Thread singleThread;

    private HandlerChain handlerChain;

    private ReentrantLock reentrantLock = new ReentrantLock();

    private Condition condition;

    //保存提交的任务
    private BlockingQueue queue = new PriorityBlockingQueue<Acceptor>();

    public ChannelTask(Configuration configuration) {
        this.configuration = configuration;
        this.handlerChain = configuration.getChannelHandlerChain();
        this.condition = reentrantLock.newCondition();
    }

    /**
     * 处理通道的逻辑
     */
    public void run() {
        if (singleThread == null) {
            singleThread = Thread.currentThread();
        }
        while (true) {
            if (!queue.isEmpty()) {
                for (int i = 0; i < queue.size(); i++) {
                    try {
                        ChannelAcceptor take = (ChannelAcceptor)queue.take();
                        SocketChannel socketChannel = take.getTarget();
                        //调用处理链处理客户端通道
                        handlerChain.proceed(socketChannel);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //没有任务处理，线程陷入沉睡
            await();
        }
    }

    /**
     * 给任务队列添加一个任务
     * @param a
     */
    public void addTask(Acceptor a) {
        try {
            this.queue.put(a);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 线程等待任务
     */
    public void await() {
        reentrantLock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 线程唤醒，处理任务
     */
    public void signal() {
        reentrantLock.lock();
        try {
            condition.signal();
        } finally {
            reentrantLock.unlock();
        }
    }

}
