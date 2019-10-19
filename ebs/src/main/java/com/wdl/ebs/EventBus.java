package com.wdl.ebs;

import com.wdl.ebs.annotation.Subscriber;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * Create by: wdl at 2019/10/19 14:48
 * 事件发布/订阅框架-通信(高度解耦)
 */
@SuppressWarnings("unused")
final public class EventBus
{
    private static volatile EventBus instance;
    private static final EventBusBuilder DEFAULT_BUILDER = new EventBusBuilder();

    private boolean eventInheritance;
    private final ExecutorService executorService;
    // 检测此事件类型的方法是否已经添加过
    final Map<Class, Object> anyMethodByEventType = new HashMap<>();
    // 订阅类 订阅类中所有的订阅方法
    private static final Map<Class<?>, List<SubscriberMethod>> METHODS_CACHE = new ConcurrentHashMap<>();
    // KEY为EventType VALUE为订阅信息列表
    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;
    // KEY为订阅者类,VALUE为此类中所有的事件类型
    private final Map<Object, List<Class<?>>> typesBySubscriber;
    // 粘性事件
    private final Map<Class<?>, Object> stickyEvent;

    public static EventBus getInstance()
    {
        if (instance == null)
        {
            synchronized (EventBus.class)
            {
                if (instance == null)
                {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    /**
     * public构造器，可设置不同的事件总线；总线之间互不干扰
     */
    public EventBus()
    {
        this(DEFAULT_BUILDER);
    }

    EventBus(EventBusBuilder builder)
    {
        this.subscriptionsByEventType = new HashMap<>();
        this.typesBySubscriber = new HashMap<>();
        this.stickyEvent = new ConcurrentHashMap<>();
        this.eventInheritance = builder.eventInheritance;
        this.executorService = builder.executorService;
    }

    /**
     * 注册订阅信息
     *
     * @param subscriber 订阅类
     */
    public void register(Object subscriber)
    {
        Class<?> subscriberClass = subscriber.getClass();
        // 查找订阅类中所有的订阅方法
        List<SubscriberMethod> methods = findUsingReflection(subscriberClass);
        // 循环订阅
        synchronized (this)
        {
            for (SubscriberMethod method : methods)
            {
                subscribe(subscriber, method);
            }
        }

    }

    /**
     * 执行事件
     *
     * @param eventType 事件类型
     */
    public void post(Object eventType)
    {
        List<Subscription> subscriptions = subscriptionsByEventType.get(eventType.getClass());
        if (subscriptions == null || subscriptions.isEmpty())
        {
            return;
        }
        for (Subscription subscription : subscriptions)
        {
            try
            {
                subscription.method.method.invoke(subscription.subscriber, eventType);
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * 订阅
     *
     * @param subscribe 订阅类
     * @param method    订阅类中的订阅方法
     */
    private void subscribe(Object subscribe, SubscriberMethod method)
    {
        Class<?> eventType = method.eventType;
        Subscription subscription = new Subscription(subscribe, method);
        // 判断此订阅关系是否已经存在
        CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
        if (subscriptions == null)
        {
            subscriptions = new CopyOnWriteArrayList<>();
            subscriptionsByEventType.put(eventType, subscriptions);
        } else
        {
            if (subscriptions.contains(subscription))
            {
                throw new RuntimeException("已经订阅过了");
            }
        }
        subscriptions.add(subscription);


        // 判断并添加
        List<Class<?>> types = typesBySubscriber.get(subscribe);
        if (types == null)
        {
            types = new ArrayList<>();
            typesBySubscriber.put(subscribe, types);
        }
        types.add(eventType);

        // 处理粘性事件
        if (method.isSticky)
        {
            if (eventInheritance)
            {

            } else
            {

            }
        }
    }

    /**
     * 查找类下的所有订阅方法
     *
     * @param subscriberClass 订阅类
     * @return 订阅类中所有的订阅方法的列表
     */
    private List<SubscriberMethod> findUsingReflection(Class<?> subscriberClass)
    {
        List<SubscriberMethod> methods = METHODS_CACHE.get(subscriberClass);
        if (methods != null)
        {
            return methods;
        } else
        {
            // 无缓存即未注册,查找订阅方法并注册
            methods = findUsingReflectionInClass(subscriberClass);
        }

        if (methods.isEmpty())
        {
            throw new NullPointerException("未找到被@Subscriber注解的方法...");
        } else
        {
            METHODS_CACHE.put(subscriberClass, methods);
            return methods;
        }
    }

    /**
     * 实际的查找方法
     *
     * @param subscriberClass 订阅类
     * @return List<SubscriberMethod>
     */
    private List<SubscriberMethod> findUsingReflectionInClass(Class<?> subscriberClass)
    {
        Method[] methods;
        List<SubscriberMethod> subscriberMethods = new ArrayList<>();
        try
        {
            // 或者类自身的方法/重载的方法
            methods = subscriberClass.getDeclaredMethods();
        } catch (Throwable e)
        {
            methods = subscriberClass.getMethods();
        }
        // 遍历所有方法->获取被注解修饰的方法
        for (Method method : methods)
        {
            // 获取方法修饰类型
            int modifiers = method.getModifiers();
            // public 修饰
            if ((modifiers & Modifier.PUBLIC) != 0)
            {
                // 获取参数,判断是否只有一个参数
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 1)
                {
                    // 获取注解
                    Subscriber subscriber = method.getAnnotation(Subscriber.class);
                    if (subscriber != null)
                    {
                        // 获取事件类型
                        Class<?> eventType = params[0];
                        // 判断此事件类型的此方法是否已经添加过
                        Object existing = anyMethodByEventType.put(eventType, method);
                        if (existing == null)
                        {
                            ThreadMode threadMode = subscriber.threadMode();
                            boolean sticky = subscriber.sticky();
                            subscriberMethods.add(new SubscriberMethod(eventType, method, sticky, threadMode));
                        }
                    }
                } else
                {
                    throw new IllegalArgumentException("注解修饰的方法参数只能为一个");
                }
            }
//            } else
//            {
//                throw new RuntimeException("注解修饰的方法参数只能被PUBLIC所修饰");
//            }

        }
        return subscriberMethods;
    }

    /**
     * unregister
     *
     * @param subscriber 订阅类
     */
    public synchronized void unregister(Object subscriber)
    {
        List<Class<?>> types = typesBySubscriber.get(subscriber);
        if (types != null)
        {
            for (Class<?> type : types)
            {
                unsubscribeByEventType(subscriber, type);
            }
            types.remove(subscriber);
        }
    }

    /**
     * 解除订阅
     *
     * @param subscriber 订阅者
     * @param type       事件类型
     */
    private void unsubscribeByEventType(Object subscriber, Class<?> type)
    {
        List<Subscription> subscriptions = subscriptionsByEventType.get(type);
        if (subscriptions != null)
        {
            int size = subscriptions.size();
            for (int i = 0; i < size; i++)
            {
                Subscription subscription = subscriptions.get(i);
                if (subscription.subscriber == subscriber)
                {
                    subscriptions.remove(i);
                    i--;
                    size--;
                }
            }
        }
    }


}
