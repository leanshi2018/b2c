package com.framework.loippi.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by longbh on 2016/12/16.
 */
public class CacheUtils {

    private Map<String, Data> caches;

    private static CacheUtils cacheUtils;

    public static CacheUtils get() {
        if (cacheUtils == null) {
            cacheUtils = new CacheUtils();
        }
        return cacheUtils;
    }

    public CacheUtils() {
        caches = new HashMap<>();
    }

    public void add(String key, Object data) {
        Data cacheData = new Data();
        cacheData.saveTime = 0l;
        cacheData.expireTime = Long.MAX_VALUE;
        cacheData.data = data;
        caches.put(key, cacheData);
    }

    public void add(String key, Object data, Integer exprence) {
        Data cacheData = new Data();
        cacheData.saveTime = System.currentTimeMillis();
        cacheData.expireTime = Long.valueOf(exprence*1000);
        cacheData.data = data;
        caches.put(key, cacheData);
    }

    public Object get(String key) {
        Data data = caches.get(key);
        if(data == null){
            return null;
        }
        if (System.currentTimeMillis() - data.saveTime > data.expireTime) {
            return null;
        }
        return data.data;
    }

    public void delete(String key) {
        caches.remove(key);
    }

    public class Data {
        Long saveTime;
        Long expireTime;
        Object data;
    }

}
