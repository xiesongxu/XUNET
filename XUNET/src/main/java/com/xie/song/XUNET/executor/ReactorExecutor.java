package com.xie.song.XUNET.executor;

import com.xie.song.XUNET.config.Configuration;
import com.xie.song.XUNET.executor.Executor;

import java.util.concurrent.ExecutorService;

/**
 * 执行服务反应任务
 */
public class ReactorExecutor implements Executor {

    private Configuration configuration;

    public ReactorExecutor() {

    }

    public ReactorExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 通过线程池执行任务
     * @param r
     */
    public void execute(Runnable r) {
        ExecutorService reactorThreadPool = configuration.getReactorThreadPool();
        reactorThreadPool.execute(r);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
