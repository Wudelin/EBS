package com.wdl.ebs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create by: wdl at 2019/10/19 15:42
 * 初始化一些EventBus的变量
 */
@SuppressWarnings("unused")
public class EventBusBuilder
{
    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    ExecutorService executorService = DEFAULT_EXECUTOR_SERVICE;

    /**
     * 父类订阅的事件是否接收
     */
    boolean eventInheritance = true;

    EventBusBuilder()
    {
    }

    public EventBusBuilder eventInheritance(boolean inheritance){
        this.eventInheritance = inheritance;
        return this;
    }

    public EventBusBuilder executorService(ExecutorService executorService){
        this.executorService = executorService;
        return this;
    }
}
