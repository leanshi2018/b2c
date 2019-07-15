package com.framework.loippi.queue;

import com.framework.loippi.utils.RedisUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于redis的命令队列
 * <p>
 * <pre>
 * 采用pub / sub机制实现
 * </pre>
 *
 * @author tangzz
 * @createDate 2016年1月15日
 */
public class EventSubscriber {

    private ExecutorService executor;
    private QueueListener subscriber;
    private static final AtomicInteger eventID = new AtomicInteger(1);

    public EventSubscriber(String brokerId, RedisUtils redis, QueueWorker worker) {
        this.executor = Executors.newFixedThreadPool(1);
        this.subscriber = new QueueListener(this.executor, brokerId, worker, redis);
    }

    public void start() {
        this.subscriber.start();
    }

    public void destroy() {
        if (executor != null) {
            executor.shutdownNow();
        }
        subscriber.stop();
    }

    private int rollEventId() {
        return eventID.getAndIncrement();
    }


}
