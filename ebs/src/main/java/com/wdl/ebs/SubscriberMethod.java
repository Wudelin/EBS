package com.wdl.ebs;

import java.lang.reflect.Method;

/**
 * Create by: wdl at 2019/10/19 15:59
 * 订阅信息的实体类
 */
@SuppressWarnings("unused")
public class SubscriberMethod
{
    /**
     * 事件类型
     */
    Class<?> eventType;
    /**
     * 订阅方法
     */
    Method method;
    /**
     * 是否为粘性事件
     */
    boolean isSticky;
    /**
     * 线程模式
     */
    ThreadMode threadMode;

    public SubscriberMethod(Class<?> eventType, Method method, boolean isSticky, ThreadMode threadMode)
    {
        this.eventType = eventType;
        this.method = method;
        this.isSticky = isSticky;
        this.threadMode = threadMode;
    }
}
