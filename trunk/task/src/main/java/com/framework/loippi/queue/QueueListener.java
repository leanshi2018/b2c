package com.framework.loippi.queue;

import com.framework.loippi.utils.RedisUtils;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;

/**
 * Created by longbh on 2017/3/9.
 */
public class QueueListener implements Runnable {

    private ExecutorService executor;
    private String channel;
    private QueueWorker handler;
    private RedisUtils redis;

    private volatile boolean isStop = false;
    /**
     * 默认为lpop方式，对应rpush
     */
    private boolean isLpop = true;
    private static final int TIMEOUT = 15;
    private static final int RETRY_INTERVAL = 10000;

    public QueueListener(ExecutorService executor, String channel, QueueWorker handler, RedisUtils redis) {
        this.executor = executor;
        this.channel = channel;
        this.handler = handler;
        this.isLpop = true;
        this.redis = redis;
    }

    public QueueListener(ExecutorService executor, String channel, QueueWorker handler, boolean isLpop, RedisUtils redis) {
        this.executor = executor;
        this.channel = channel;
        this.handler = handler;
        this.isLpop = isLpop;
        this.redis = redis;
    }

    public void start() {
        executor.submit(this);
    }

    public void stop() {
        this.isStop = true;
    }

    @Override
    public void run() {

        while (!isStop) {
            String result = null;
            try {
                if (this.isLpop) {
                    result = redis.blpop(channel, TIMEOUT, String.class);
                } else {
                    result = redis.brpop(channel, TIMEOUT, String.class);
                }
                if (result != null) {
                    handler.handle(channel, result);
                }
            } catch (Throwable e) {
                Logger.getLogger(RedisUtils.class).error("pop from queue failed: %s" + e.getMessage());
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException e1) {
                }
            }

        }
    }
}