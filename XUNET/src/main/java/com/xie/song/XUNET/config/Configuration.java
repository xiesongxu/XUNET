package com.xie.song.XUNET.config;

import com.xie.song.XUNET.exception.ExistException;
import com.xie.song.XUNET.handler.ChannelHandlerChain;
import com.xie.song.XUNET.handler.DataHandlerChain;
import com.xie.song.XUNET.handler.HandlerChain;
import com.xie.song.XUNET.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 用于配置XUNET，如果没有自定义配置，及使用默认配置
 */
public class Configuration {
    //服务端线程
    private ExecutorService reactorThreadPool;

    //客户端线程
    private ExecutorService workerThreadPool;

    //接受数据处理线程
    private ExecutorService eventThreadPool;

    //处理通道的线程
    private ExecutorService channelThreadPool;

    //通道的处理链
    private HandlerChain channelHandlerChain;

    //数据的处理链
    private HandlerChain dataHandlerChain;

    //判断框架是以什么方式来启动
    private boolean isServer;

    //存储客户端地址对象
    private List<ClientIpAddress> clientAddresses = new ArrayList<ClientIpAddress>();

    //存储服务端地址对象
    private List<ServerIpAddress> serverAddresses = new ArrayList<ServerIpAddress>();


    //服务端线程池大小为2
    private int reactorNum = 2;

    //客户端线程池大小为6
    private int workerNum = 4;

    //处理数据的线程池大小
    private int eventNum = 8;

    //处理通道的线程池大小
    private int channelNum = 8;

    //记录反应任务，用于服务端的接受连接
    private ReactorTask[] reactorTasks;

    //记录工作任务，用于客户端接受数据和发送数据
    private WorkerTask[] workerTasks;

    //记录通道任务，处理接受到的客户端通道
    private ChannelTask[] channelTasks;

    //记录数据任务，处理客户端接受的数据
    private DataTask[] dataTasks;

    //取ReactorTask任务的下标
    private volatile int reactorTaskIndex = 0;

    //取WorkerTask任务的下标
    private volatile int workerTaskIndex = 0;

    //取dataTask任务的下标
    private volatile int dataTaskIndex = 0;

    //取channelTask任务的下标
    private volatile int channelTaskIndex = 0;

    public Configuration() {
        reactorTasks = new ReactorTask[reactorNum];
        workerTasks = new WorkerTask[workerNum];
        channelTasks = new ChannelTask[channelNum];
        dataTasks = new DataTask[eventNum];
    }

    /**
     * 初始化各个线程池大小
     * 和处理链
     *
     */
    public void init(){

        this.reactorThreadPool = Executors.newFixedThreadPool(reactorNum);

        this.workerThreadPool = Executors.newFixedThreadPool(workerNum);

        this.eventThreadPool = Executors.newFixedThreadPool(eventNum);

        this.channelThreadPool = Executors.newFixedThreadPool(channelNum);

        this.dataHandlerChain = new DataHandlerChain(this);

        this.channelHandlerChain = new ChannelHandlerChain(this);
    }

    /**
     * 初始化数据处理器链
     */
    public void initDataHandlerChain() {
        dataHandlerChain.init();
    }

    /**
     * 检查通道处理链 和 数据处理链是否空
     * 如果为空，返回true
     * @return
     */
    public boolean inspect() {
        return channelHandlerChain.isEmpty() || dataHandlerChain.isEmpty();
    }

    /**
     * 把服务端绑定器提交到指定的任务队列中
     * @param serverChannelBinder 服务端绑定器
     */
    public void doServerBinderToTaskQueue(Binder serverChannelBinder) {
        synchronized (reactorTasks) {
            int length = reactorTasks.length;
            //获取反射任务
            ReactorTask reactorTask = reactorTasks[reactorTaskIndex & (length - 1)];
            reactorTaskIndex++;
            //添加服务绑定器
            reactorTask.addTask(serverChannelBinder);
            reactorTask.getSelector().wakeup();
        }
    }

    /**
     * 把客户端绑定器提交到指定的任务队列中
     * @param binder
     */
    public void doClientBinderToTaskQueue(Binder binder) {
        synchronized (workerTasks) {
            int length = workerTasks.length;
            WorkerTask workerTask = workerTasks[workerTaskIndex & (length - 1)];
            workerTaskIndex++;
            //添加客户端绑定器
            workerTask.addTask(binder);
            workerTask.getSelector().wakeup();
        }
    }


    /**
     * 把 通道接受器 传入 通道任务队列 中
     * @param acceptor
     */
    public void doChannelAcceptorToTaskQueue(Acceptor acceptor) {
        synchronized (channelTasks) {
            ChannelTask channelTask = channelTasks[channelTaskIndex & (channelNum - 1)];
            channelTaskIndex++;
            channelTask.addTask(acceptor);
            channelTask.signal();
        }
    }

    /**
     * 把 数据接收器 传入 数据任务队列中
     * @param acceptor
     */
    public void doDataAcceptorToTaskQueue(Acceptor acceptor) {
        synchronized (dataTasks) {
            DataTask dataTask = dataTasks[dataTaskIndex & (eventNum - 1)];
            dataTaskIndex++;
            dataTask.addTask(acceptor);
            dataTask.signal();
        }
    }

    public ExecutorService getReactorThreadPool() {
        return reactorThreadPool;
    }

    public void setReactorThreadPool(ExecutorService reactorThreadPool) {
        this.reactorThreadPool = reactorThreadPool;
    }

    public ExecutorService getWorkerThreadPool() {
        return workerThreadPool;
    }

    public void setWorkerThreadPool(ExecutorService workerThreadPool) {
        this.workerThreadPool = workerThreadPool;
    }

    public ExecutorService getEventThreadPool() {
        return eventThreadPool;
    }

    public void setEventThreadPool(ExecutorService eventThreadPool) {
        this.eventThreadPool = eventThreadPool;
    }

    public ExecutorService getChannelThreadPool() {
        return channelThreadPool;
    }

    public void setChannelThreadPool(ExecutorService channelThreadPool) {
        this.channelThreadPool = channelThreadPool;
    }

    public HandlerChain getChannelHandlerChain() {
        return channelHandlerChain;
    }

    public void setChannelHandlerChain(HandlerChain channelHandlerChain) {
        this.channelHandlerChain = channelHandlerChain;
    }

    public HandlerChain getDataHandlerChain() {
        return dataHandlerChain;
    }

    public void setDataHandlerChain(HandlerChain dataHandlerChain) {
        this.dataHandlerChain = dataHandlerChain;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }


    public int getReactorNum() {
        return reactorNum;
    }

    public void setReactorNum(int reactorNum) {
        this.reactorNum = reactorNum;
    }

    public int getWorkerNum() {
        return workerNum;
    }

    public void setWorkerNum(int workerNum) {
        this.workerNum = workerNum;
    }

    public int getEventNum() {
        return eventNum;
    }

    public void setEventNum(int eventNum) {
        this.eventNum = eventNum;
    }

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public ReactorTask[] getReactorTasks() {
        return reactorTasks;
    }

    public void setReactorTasks(ReactorTask[] reactorTask) {
        this.reactorTasks = reactorTask;
    }

    public WorkerTask[] getWorkerTasks() {
        return workerTasks;
    }

    public void setWorkerTasks(WorkerTask[] workerTasks) {
        this.workerTasks = workerTasks;
    }

    public ChannelTask[] getChannelTasks() {
        return channelTasks;
    }

    public void setChannelTasks(ChannelTask[] channelTasks) {
        this.channelTasks = channelTasks;
    }

    public DataTask[] getDataTasks() {
        return dataTasks;
    }

    public void setDataTasks(DataTask[] dataTasks) {
        this.dataTasks = dataTasks;
    }

    public List<ClientIpAddress> getClientAddresses() {
        return clientAddresses;
    }

    public void addClientAddresses(ClientIpAddress clientAddresses) {
        this.clientAddresses.add(clientAddresses);
    }

    public List<ServerIpAddress> getServerAddresses() {
        return serverAddresses;
    }

    public void addServerAddresses(ServerIpAddress serverAddresses) {
        this.serverAddresses.add(serverAddresses);
    }



}
