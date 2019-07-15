package com.framework.loippi.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

/**
 * redis连接实例
 *
 * @author tangzz
 * @createDate 2015年10月22日
 */
public class RedisClusterUtils {
    private static RedisClusterUtils instance;
    private JedisCluster pool;

    private String host;
    private int port;

    public RedisClusterUtils(Set<HostAndPort> portList, int timeout, JedisPoolConfig config) {
        this.pool = new JedisCluster(portList, timeout, config);
    }

    public void ping() {
        try {
            if (!"PONG".equals(pool.ping())) {
                Logger.getLogger(RedisClusterUtils.class).error("[REDIS]failed to connect %s:%d" + host + port);
            } else {
                Logger.getLogger(RedisClusterUtils.class).info("[REDIS]connect to %s:%d." + host + port);
            }
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("[REDIS]failed to connect %s:%d" + host + port);
        }
    }

    /**
     * get
     *
     * @param key
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            String value = pool.get(key);
            return JacksonUtil.fromJson(value, clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("get (%s) failed" + key);
            return null;
        }
    }

    /**
     * set
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        try {
            pool.set(key, JacksonUtil.toJson(value));
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("get (%s) failed" + key);
        }
    }

    /**
     * del
     *
     * @param key
     */
    public void del(String key) {
        try {
            pool.del(key);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("get (%s) failed" + key);
        }
    }

    /**
     * incr
     *
     * @param key
     */
    public long incr(String key, long incr) {
        try {
            return pool.incrBy(key, incr);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("get (%s) failed" + key);
            return 0l;
        }
    }

    /**
     * 队列方法-lpush
     *
     * @param key
     * @param value
     */
    public void lpush(String key, Object value) {
        try {
            pool.lpush(key, JacksonUtil.toJson(value));
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("lpush data to queue(%s) failed, objectClass: %s" + key + value.getClass().getName());
        }
    }

    /**
     * 队列方法-rpush
     *
     * @param key
     * @param value
     */
    public void rpush(String key, Object value) {
        try {
            pool.rpush(key, JacksonUtil.toJson(value));
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("rpush data to queue(%s) failed, objectClass: %s" + key + value.getClass().getName());
        }
    }

    /**
     * 队列方法-lpop
     *
     * @param key
     * @param clazz
     * @return
     */
    public <T> T lpop(String key, Class<T> clazz) {
        try {
            String value = pool.lpop(key);
            if (value == null) {
                return null;
            }
            return JacksonUtil.fromJson(value, clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("lpop data from queue(%s) failed" + key);
            return null;
        }
    }

    /**
     * 队列方法-rpop
     *
     * @param key
     * @param clazz
     * @return
     */
    public <T> T rpop(String key, Class<T> clazz) {
        try {
            String value = pool.rpop(key);
            if (value == null) {
                return null;
            }
            return JacksonUtil.fromJson(value, clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("rpop data from queue(%s) failed" + key);
            return null;
        }
    }

    /**
     * 队列方法-blpop
     *
     * @param key
     * @param timeout
     * @return
     */
    public <T> T blpop(String key, int timeout, Class<T> clazz) {
        try {
            List<String> results = pool.blpop(key);
            if (results == null || results.size() < 2) {
                return null;
            }

            return JacksonUtil.fromJson(results.get(1), clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("blpop data from queue(%s) failed" + key);
            throw new RuntimeException(e);
        }
    }

    /**
     * 队列方法-brpop
     *
     * @param key
     * @param timeout
     * @return
     */
    public <T> T brpop(String key, int timeout, Class<T> clazz) {
        try {
            List<String> results = pool.brpop(key);
            if (results == null || results.size() < 2) {
                return null;
            }

            return JacksonUtil.fromJson(results.get(1), clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("brpop data from queue(%s) failed" + key);
            throw new RuntimeException(e);
        }
    }

    /**
     * sorted set -count
     *
     * @param key
     * @return
     */
    public long zcount(String key) {
        try {
            return pool.zcard(key);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("zcount (%s) failed" + key);
            return 0;
        }
    }

    /**
     * sorted set -add
     *
     * @param key
     * @param score
     * @param value
     */
    public void zadd(String key, long score, String value) {
        try {
            pool.zadd(key, score, value);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("zadd (%s) failed" + key);
        }
    }

    /**
     * sorted set -range
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> zrange(String key, int start, int end) {
        try {
            Set<String> set = pool.zrange(key, start, end);
            return new ArrayList<String>(set);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("zrange (%s) failed" + key);
            return null;
        }
    }

    /**
     * sorted set - remove
     *
     * @param key
     * @param value
     * @return
     */
    public boolean zremove(String key, String value) {
        try {
            return pool.zrem(key, value) > 0;
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("zremove (%s) failed" + key);
            return false;
        }
    }

    /**
     * hash - set
     *
     * @param key
     * @param value
     * @return
     */
    public boolean hset(String key, String field, Object value) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field) || value == null) {
            return false;
        }

        try {
            pool.hset(key, field, value.toString());
            return true;
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("hset (%s) failed" + key);
            return false;
        }
    }

    /**
     * hash - mset
     *
     * @param key
     * @return
     */
    public boolean hmset(String key, Map<String, Object> values) {
        if (StringUtils.isEmpty(key) || values == null || values.isEmpty()) {
            return false;
        }

        try {
            Map<String, String> hash = new HashMap<String, String>();
            for (Map.Entry<String, Object> e : values.entrySet()) {
                Object v = e.getValue();
                if (v == null) {
                    continue;
                }
                hash.put(e.getKey(), v.toString());
            }

            pool.hmset(key, hash);
            return true;
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("hset (%s) failed" + key);
            return false;
        }
    }

    /**
     * hash - del
     *
     * @param key
     * @param field
     * @return
     */
    public boolean hdel(String key, String field) {
        try {
            return pool.hdel(key, field) > 0;
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("hdel (%s) failed" + key);
            return false;
        }
    }

    /**
     * hash - del
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public long hincr(String key, String field, long value) {
        try {
            return pool.hincrBy(key, field, value);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("hdel (%s) failed" + key);
            return 0l;
        }
    }

    /**
     * hash - get
     *
     * @param key
     * @param field
     * @return
     */
    public <T> T hget(String key, String field, Class<T> clazz) {
        try {
            String str = pool.hget(key, field);
            return JacksonUtil.fromJson(str, clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("hget (%s) failed" + key);
            return null;
        }
    }

    /**
     * hash - getAll
     *
     * @param key
     * @return
     */
    public <T> T hgetAll(String key, Class<T> clazz) {
        try {
            Map<String, String> map = pool.hgetAll(key);

            if (map == null || map.isEmpty()) {
                return null;
            }
            return JacksonUtil.fromJson(JacksonUtil.toJson(map), clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisClusterUtils.class).error("hgetAll (%s) failed" + key);
            return null;
        }
    }

}
