package com.framework.loippi.service.impl;

import com.framework.loippi.service.RedisService;
import com.framework.loippi.utils.RedisUtils;
import com.framework.loippi.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

/**
 * redisservice
 * Created by longbh on 16/9/6.
 */
@Service("redisServiceImpl")
public class RedisServiceImpl implements RedisService {

    ///--------redis-------------
    private Boolean redisEnabled = true;
    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private Integer port;
    @Value("${redis.maxIdle}")
    private Integer maxIdle;
    @Value("${redis.maxTotal}")
    private Integer maxTotal;
    @Value("${redis.maxWaitMillis}")
    private Long maxWaitMillis;
    @Value("${redis.timeout}")
    private Integer timeout;
    @Value("${redis.pass}")
    private String password;
    private RedisUtils redisUtils;

    @PostConstruct
    public void init() {
        JedisPoolConfig config = new JedisPoolConfig();
        // 连接池配置
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxTotal);
        config.setMaxWaitMillis(maxWaitMillis);
        redisUtils = new RedisUtils(config, host, port, timeout, password);
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    public String get(String key) {
        if (StringUtil.isEmpty(key)) return null;
        return redisUtils.get(key);
    }

    /**
     * 设置单个值，并设置失效时间，单位时间为秒
     *
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public void set(String key, String value, int seconds) {
        if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value) || seconds == 0) return;
        redisUtils.set(key, value, seconds);
    }

    @Override
    public void delete(String key) {
        if (key == null) return;
        redisUtils.del(key);
    }

    /**
     * 添加缓存
     *
     * @param key
     * @param object
     */
    @Override
    public void save(String key, Object object) {
        if (key == null) return;
        redisUtils.set(key, object);
    }


    @Override
    public <T> T get(String key, Class<T> type) {
        return redisUtils.get(key, type);
    }

    public RedisUtils getRedisUtils() {
        return redisUtils;
    }

}
