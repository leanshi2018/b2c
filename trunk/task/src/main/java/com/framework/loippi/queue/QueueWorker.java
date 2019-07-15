package com.framework.loippi.queue;

/**
 * Created by longbh on 2017/3/9.
 */
public interface QueueWorker {
    void handle(String key, String data);
}
