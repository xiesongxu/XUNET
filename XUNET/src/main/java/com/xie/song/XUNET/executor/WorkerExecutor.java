package com.xie.song.XUNET.executor;

import com.xie.song.XUNET.config.Configuration;

import java.util.concurrent.ExecutorService;

/**
 * 执行客户端接受数据任务
 */
public class WorkerExecutor implements Executor {

    private Configuration configuration;

    public WorkerExecutor() {

    }

    public WorkerExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    public void execute(Runnable r) {
        ExecutorService workerThreadPool = configuration.getWorkerThreadPool();
        workerThreadPool.execute(r);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
