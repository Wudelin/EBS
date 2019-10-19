package com.wdl.ebs.annotation;

import com.wdl.ebs.ThreadMode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by: wdl at 2019/10/19 15:23
 * 订阅者
 * 可指定线程模式-是否为黏性事件等
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@SuppressWarnings("unused")
public @interface Subscriber
{
    /**
     * 是否为粘性事件
     *
     * @return 默认false
     */
    boolean sticky() default false;

    /**
     * 指定订阅者执行Method的线程
     *
     * @return 默认主线程
     */
    ThreadMode threadMode() default ThreadMode.MAIN;
}
