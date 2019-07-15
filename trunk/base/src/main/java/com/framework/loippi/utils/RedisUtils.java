package com.framework.loippi.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.*;

/**
 * redis连接实例
 *
 * @author tangzz
 * @createDate 2015年10月22日
 */
public class RedisUtils {
    private static RedisUtils instance;
    private JedisPool pool;

    private String host;
    private int port;

    public RedisUtils(JedisPoolConfig config, String host, int port, int timeout, String pass) {
        this.pool = new JedisPool(config, host, port, timeout, pass);
        this.host = host;
        this.port = port;
    }

    public void ping() {
        try {
            Jedis jedis = getClient();
            if (!"PONG".equals(jedis.ping())) {
                Logger.getLogger(RedisUtils.class).error("[REDIS]failed to connect %s:%d" + host + port);
                jedis = null;
            } else {
                Logger.getLogger(RedisUtils.class).info("[REDIS]connect to %s:%d." + host + port);
            }
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("[REDIS]failed to connect %s:%d" + host + port);
        }
    }

    private Jedis getClient() {
        if (pool == null) {
            throw new IllegalStateException("redis pool has not been initialized.");
        }
        return pool.getResource();
    }

    /**
     * 停止
     */
    public void stop() {
        if (pool == null) {
            return;
        }

        try {
            pool.destroy();
        } catch (Throwable e) {
        } finally {
            pool = null;
        }
    }

    /**
     * 获取字符串
     * @param key
     * @return
     *
     */
    public String get(String key){
        Jedis jedis = null;
        try {
            jedis = getClient();
           return jedis.get(key);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("get (%s) failed" + key);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 设置单个值，并设置失效时间，单位时间为秒
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public void set(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.set(key,value);
            jedis.expire(key,seconds);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("get (%s) failed" + key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }



    /**
     * get
     *
     * @param key
     * @param clazz
     */
    public <T> T get(String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            String value = jedis.get(key);
            System.out.println(value);
            System.out.println("xxx");
            return JacksonUtil.fromJson(value, clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("get (%s) failed" + key);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }



    /**
     * set
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.set(key, JacksonUtil.toJson(value));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * del
     *
     * @param key
     */
    public void del(String key) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.del(key);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("get (%s) failed" + key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * incr
     *
     * @param key
     */
    public long incr(String key, long incr) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            return jedis.incrBy(key, incr);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("get (%s) failed" + key);
            return 0l;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 队列方法-lpush
     *
     * @param key
     * @param value
     */
    public void lpush(String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.lpush(key, JacksonUtil.toJson(value));
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("lpush data to queue(%s) failed, objectClass: %s" + key + value.getClass().getName());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 队列方法-rpush
     *
     * @param key
     * @param value
     */
    public void rpush(String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.rpush(key, JacksonUtil.toJson(value));
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("rpush data to queue(%s) failed, objectClass: %s" + key + value.getClass().getName());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
        Jedis jedis = null;
        try {
            jedis = getClient();
            String value = jedis.lpop(key);
            if (value == null) {
                return null;
            }
            return JacksonUtil.fromJson(value, clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("lpop data from queue(%s) failed" + key);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
        Jedis jedis = null;
        try {
            jedis = getClient();
            String value = jedis.rpop(key);
            if (value == null) {
                return null;
            }
            return JacksonUtil.fromJson(value, clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("rpop data from queue(%s) failed" + key);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
        Jedis jedis = null;
        try {
            jedis = getClient();

            List<String> results = jedis.blpop(timeout, key);
            if (results == null || results.size() < 2) {
                return null;
            }

            return JacksonUtil.fromJson(results.get(1), clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("blpop data from queue(%s) failed" + key);
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
        Jedis jedis = null;
        try {
            jedis = getClient();

            List<String> results = jedis.brpop(timeout, key);
            if (results == null || results.size() < 2) {
                return null;
            }

            return JacksonUtil.fromJson(results.get(1), clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("brpop data from queue(%s) failed" + key);
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * sorted set -count
     *
     * @param key
     * @return
     */
    public long zcount(String key) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            return jedis.zcard(key);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("zcount (%s) failed" + key);
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.zadd(key, score, value);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("zadd (%s) failed" + key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
        Jedis jedis = null;
        try {
            jedis = getClient();
            Set<String> set = jedis.zrange(key, start, end);
            return new ArrayList<String>(set);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("zrange (%s) failed" + key);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
        Jedis jedis = null;
        try {
            jedis = getClient();
            return jedis.zrem(key, value) > 0;
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("zremove (%s) failed" + key);
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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

        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.hset(key, field, value.toString());
            return true;
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("hset (%s) failed" + key);
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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

        Jedis jedis = null;
        try {
            Map<String, String> hash = new HashMap<String, String>();
            for (Map.Entry<String, Object> e : values.entrySet()) {
                Object v = e.getValue();
                if (v == null) {
                    continue;
                }
                hash.put(e.getKey(), v.toString());
            }

            jedis = getClient();
            jedis.hmset(key, hash);
            return true;
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("hset (%s) failed" + key);
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
        Jedis jedis = null;
        try {
            jedis = getClient();
            return jedis.hdel(key, field) > 0;
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("hdel (%s) failed" + key);
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
        Jedis jedis = null;
        try {
            jedis = getClient();
            return jedis.hincrBy(key, field, value);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("hdel (%s) failed" + key);
            return 0l;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
        Jedis jedis = null;
        try {
            jedis = getClient();
            String str = jedis.hget(key, field);
            return JacksonUtil.fromJson(str, clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("hget (%s) failed" + key);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * hash - getAll
     *
     * @param key
     * @return
     */
    public <T> T hgetAll(String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            Map<String, String> map = jedis.hgetAll(key);

            if (map == null || map.isEmpty()) {
                return null;
            }
            return JacksonUtil.fromJson(JacksonUtil.toJson(map), clazz);
        } catch (Exception e) {
            Logger.getLogger(RedisUtils.class).error("hgetAll (%s) failed" + key);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 订阅
     * <p>
     * <pre>
     * 注意该方法为阻塞方法，可在线程池中开启
     * </pre>
     *
     * @param sub
     * @param patterns
     */
    public void psubscribe(JedisPubSub sub, String patterns) {
        Jedis jedis = null;
        try {
            jedis = getClient();
            jedis.psubscribe(sub, patterns);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


}
