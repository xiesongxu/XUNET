package com.xie.song.XUNET.executor;

import com.xie.song.XUNET.config.Configuration;

/**
 * 执行器，用于执行提交的任务
 */
public interface Executor {

    /**
     * 执行任务的逻辑实现
     * @param r
     */
    public void execute(Runnable r);

    /**
     * 设置配置类
     * @param configuration
     */
    void setConfiguration(Configuration configuration);

}
