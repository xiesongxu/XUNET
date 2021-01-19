package com.xie.song.XUNET.executor;

import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.executor.Executor;

import java.util.concurrent.ExecutorService;

/**
 * 执行数据任务
 */
public class DataExecutor implements Executor {

    private Configuration configuration;

    public DataExecutor() {

    }

    public DataExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 执行方法
     * @param r
     */
    public void execute(Runnable r) {
        ExecutorService threadPool = configuration.getEventThreadPool();
        threadPool.execute(r);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
