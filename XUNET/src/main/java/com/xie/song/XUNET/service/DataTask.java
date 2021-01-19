package com.xie.song.XUNET.service;

import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.handler.HandlerChain;

import java.nio.channels.Selector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 把数据处理器包装成一个任务
 */
public class DataTask implements Runnable {

    private Configuration configuration;

    private Thread singleThread;

    //数据处理链
    private HandlerChain handlerChain;

    private ReentrantLock reentrantLock = new ReentrantLock();

    private Condition condition;

    //保存提交的任务
    private BlockingQueue queue = new PriorityBlockingQueue<Acceptor>();

    public DataTask(Configuration configuration) {
        this.configuration = configuration;
        this.handlerChain = configuration.getDataHandlerChain();
        this.condition = reentrantLock.newCondition();
    }

    /**
     * 处理数据的逻辑
     */
    public void run() {
        if (singleThread == null) {
            singleThread = Thread.currentThread();
        }
        while (true) {
            if (!queue.isEmpty()) {
                for (int i = 0; i < queue.size(); i++) {
                    try {
                        Acceptor DataAcceptor = (Acceptor) queue.take();
                        DataOrigin target = (DataOrigin) DataAcceptor.getTarget();
                        if (target == null) {
                            continue;
                        }
                        //开始处理数据
                        handlerData(target);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            await();
        }
    }

    /**
     * 处理数据源
     * @param target
     */
    public void handlerData(DataOrigin target) {
        if (target.isRead()) {
            //把数据经过处理器处理后，进行读取
            handlerChain.read(new Datagram(true,target.getBytes(),target.getSocketChannel()),null);
        } else {
            //把数据经过处理器处理后，进行写出
            handlerChain.write(new Datagram(false,target.getSocketChannel()),null);
        }
    }

    /**
     * 添加任务
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
