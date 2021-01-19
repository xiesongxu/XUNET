package com.xie.song.XUNET.service;

import com.xie.song.XUNET.config.ClientIpAddress;
import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.config.ServerIpAddress;
import com.xie.song.XUNET.executor.*;

import java.util.List;

/**
 * 提供基本服务
 */
public class StandardService {

    //服务任务执行器
    private Executor reactor = new ReactorExecutor();

    //客户端任务执行器
    private Executor worker = new WorkerExecutor();

    //通道执行器
    private Executor channel = new ChannelExecutor();

    //数据执行器
    private Executor data = new DataExecutor();

    /**
     * 标准服务开始
     * @param configuration
     */
    public void start(Configuration configuration) {
        initTaskQueueByIsServer(configuration);
        initAcceptorTaskQueue(configuration);
        submitBinderToTaskQueue(configuration);
    }

    /**
     * 根据是否是服务端来初始化任务
     * @param configuration 配置类
     */
    public void initTaskQueueByIsServer(Configuration configuration) {
        boolean isServer = configuration.isServer();
        if(isServer) {
            executeReactorTask(configuration);
            executeWorkerTask(configuration);
        } else {
            executeWorkerTask(configuration);
        }
    }

    /**
     * 初始化接受类型的任务队列
     * @param configuration
     */
    public void initAcceptorTaskQueue(Configuration configuration) {
        boolean isServer = configuration.isServer();
        if (isServer) {
            executeChannelTask(configuration);
            executeDataTask(configuration);
        } else {
            executeDataTask(configuration);
        }

    }

    /**
     * 执行服务端任务队列
     * @param configuration
     */
    public void executeReactorTask(Configuration configuration) {
        reactor.setConfiguration(configuration);
        int reactorNum = configuration.getReactorNum();
        for (int i = 0; i < reactorNum; i++) {
            //创建反射任务
            ReactorTask reactorTask = new ReactorTask(configuration);
            ReactorTask[] reactorTasks = configuration.getReactorTasks();
            reactorTasks[i] = reactorTask;
            //执行反射任务
            reactor.execute(reactorTask);
        }
    }

    /**
     * 执行客户端任务队列
     * @param configuration
     */
    public void executeWorkerTask(Configuration configuration) {
        worker.setConfiguration(configuration);
        int workerNum = configuration.getWorkerNum();
        for (int i = 0; i < workerNum; i++) {
            //创建工作任务
            WorkerTask workerTask = new WorkerTask(configuration,false);
            WorkerTask[] workerTasks = configuration.getWorkerTasks();
            workerTasks[i] = workerTask;
            //执行工作任务
            worker.execute(workerTask);
        }
    }

    /**
     * 执行通道处理任务队列
     * @param configuration
     */
    public void executeChannelTask(Configuration configuration) {
        channel.setConfiguration(configuration);
        int channelNum = configuration.getChannelNum();
        for (int i = 0; i < channelNum; i++) {
            //创建通道任务
            ChannelTask channelTask = new ChannelTask(configuration);
            ChannelTask[] channelTasks = configuration.getChannelTasks();
            channelTasks[i] = channelTask;
            //执行通道任务
            channel.execute(channelTask);
        }
    }

    /**
     * 执行数据处理任务队列
     * @param configuration
     */
    public void executeDataTask(Configuration configuration) {
        data.setConfiguration(configuration);
        int eventNum = configuration.getEventNum();
        for (int i = 0; i < eventNum; i++) {
            //创建数据任务
            DataTask dataTask = new DataTask(configuration);
            DataTask[] dataTasks = configuration.getDataTasks();
            dataTasks[i] = dataTask;
            //执行数据任务
            data.execute(dataTask);
        }
    }


    /**
     * 判断服务端还是客户端，分别把绑定任务提交到任务队列中
     * @param configuration 配置类
     */
    public void submitBinderToTaskQueue(Configuration configuration) {
        boolean isServer = configuration.isServer();
        //判断是否服务端
        if (isServer) {
            List<ServerIpAddress> serverAddresses = configuration.getServerAddresses();
            for (ServerIpAddress ip : serverAddresses) {
                //创建服务通道绑定器
                ServerChannelBinder serverChannelBinder = new ServerChannelBinder(ip);
                configuration.doServerBinderToTaskQueue(serverChannelBinder);
            }
        } else {
            List<ClientIpAddress> clientAddresses = configuration.getClientAddresses();
            for (ClientIpAddress ip : clientAddresses) {
                ClientChannelBinder clientChannelBinder = new ClientChannelBinder(ip);
                configuration.doClientBinderToTaskQueue(clientChannelBinder);
            }
        }
    }

}
