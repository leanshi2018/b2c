package com.framework.loippi.queue;

import com.framework.loippi.utils.RedisUtils;

/**
 * 基于redis的事件发布
 *
 * @author tangzz
 * @createDate 2016年1月15日
 */
public class EventPublisher {

    private RedisUtils redis;

    public EventPublisher(RedisUtils redis) {
        this.redis = redis;
    }

    /**
     * 发布事件
     *
     * @param event
     */
    public void publish(String event, String brokerId) {
        // 更改为rpush-blpop方式
        redis.rpush(brokerId, event);
    }

}
