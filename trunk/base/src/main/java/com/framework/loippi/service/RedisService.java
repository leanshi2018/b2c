package com.framework.loippi.service;

import com.framework.loippi.utils.RedisUtils;

/**
 * Created by longbh on 16/9/6.
 */
public interface RedisService {

    void delete(String key);

    void save(String key, Object object);

    <T> T get(String key, Class<T> type);

    /**
     * 获取字符串
     * @param key
     * @return
     */
    public String get(String key);

    /**
     * 设置单个值，并设置失效时间，单位时间为秒
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public void set(String key, String value, int seconds);

    RedisUtils getRedisUtils();

}
