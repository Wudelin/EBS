package com.wdl.ebs;

/**
 * Create by: wdl at 2019/10/19 15:28
 * 指定订阅者执行所在的线程-线程模式
 */
@SuppressWarnings("unused")
public enum ThreadMode
{
    /**
     * 主线程执行
     */
    MAIN,
    /**
     * 子线程执行，一次取出全部事件
     */
    BACKGROUND,
    /**
     * 子线程执行,一次取出一个事件
     */
    ASYNC,
    POSTING
}
