package com.xie.song.XUNET.executor;

import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.executor.Executor;

import java.util.concurrent.ExecutorService;

/**
 * 执行通道任务
 */
public class ChannelExecutor implements Executor {

    private Configuration configuration;

    public ChannelExecutor() {

    }

    public ChannelExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 执行方法
     * @param r
     */
    public void execute(Runnable r) {
        ExecutorService threadPool = configuration.getChannelThreadPool();
        threadPool.execute(r);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
