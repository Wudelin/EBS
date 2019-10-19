package com.wdl.ebs;

/**
 * Create by: wdl at 2019/10/19 16:02
 * 订阅者、被订阅者信息
 */
@SuppressWarnings("unused")
public class Subscription
{
    /**
     * 订阅者类
     */
    final Object subscriber;
    /**
     * 订阅者类中的订阅者信息
     */
    final SubscriberMethod method;

    volatile boolean isActive;

    public Subscription(Object subscriber, SubscriberMethod method)
    {
        this.subscriber = subscriber;
        this.method = method;
        this.isActive = true;
    }
}
