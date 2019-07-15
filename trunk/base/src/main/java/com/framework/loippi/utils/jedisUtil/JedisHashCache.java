package com.framework.loippi.utils.jedisUtil;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;

@Component
public class JedisHashCache extends JedisCache  {
    
	private String key = null;
	
	public JedisHashCache() {
		super();
	}
	JedisHashCache(JedisPool pool, String key ){
		super( pool );
		this.key = key;
	}
	JedisHashCache( JedisPool pool ){
		super( pool );
	
	}
	
    public long setValue( String field, String value,boolean forceNewNotExist) {
    	return setValue( key, field, value, forceNewNotExist );
    }
    
    public long setValue(String key, String field, String value, boolean forceNewNotExist) {
        Jedis jedis = null;
        try {
        	jedis = pool.getResource();
        	if( forceNewNotExist ){
        		return jedis.hsetnx( key, field, value );//存在返回0，不存在set值，返回1
        	}
            return jedis.hset( key, field, value );//存在，覆盖值，返回0，不存在set值,返回1
        } catch (Exception e) {
            e.printStackTrace();
            pool.returnBrokenResource(jedis);
        	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    public long delKey(String key, String field) {
        Jedis jedis = null;
        try {
        	jedis = pool.getResource();
        	long l = jedis.hdel(key, field);//存在，覆盖值，返回0，不存在set值,返回1
        	System.out.println(l);
            return l;
        } catch (Exception e) {
            e.printStackTrace();
            pool.returnBrokenResource(jedis);
        	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }

    public Object getValue( String field ) {
    	return getValue( key, field );
    }
    
    /**
     * 存在返回;不存在返回null；异常抛出异常
     * @param key
     * @param field
     * @return
     */
    public Object getValue(String key, String field ) {
        Jedis jedis = null;
        try {
        	jedis = pool.getResource();
        	return jedis.hget(key,field);
        } catch (Exception e) {
            e.printStackTrace();
            pool.returnBrokenResource(jedis);
        	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }
    

   
   
    public List<Object> hgetAll(String key,List<String> list) {
        Jedis jedis = pool.getResource();
        Pipeline pipeline = jedis.pipelined();
        List<Object> resultList = new ArrayList<Object>();
        try {
        	for (String str : list) {
        		pipeline.hget(key, str);
			}
        	resultList = pipeline.syncAndReturnAll();
        	return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            pool.returnBrokenResource(jedis);
            throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }
    
 
    public long incrValue( String field, long value ) {
    	return incrValue( key, field, value);
    }
    

    public long incrValue(String key, String field, long value ) {
        Jedis jedis = null;
        try {
        	jedis = pool.getResource();
       		return jedis.hincrBy( key, field, value );
        } catch (Exception e) {
        	 e.printStackTrace();
        	 pool.returnBrokenResource(jedis);
         	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    public long size(){
    	return size( key );
    }
    

    public long size( String key ){
        Jedis jedis = null;
        try {
        	jedis = pool.getResource();
        	return jedis.hlen( key );//
        } catch (Exception e) {
        	e.printStackTrace();
        	pool.returnBrokenResource(jedis);
         	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }    	
    }
    
    public long memSize(){
        Jedis jedis = null;
        try {
        	jedis = pool.getResource();
        	return jedis.dbSize();//
        } catch (Exception e) {
        	e.printStackTrace();
        	pool.returnBrokenResource(jedis);
         	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }    	
    }
    
    public long remove(String key) {
        Jedis jedis = pool.getResource();
        try
        {
            long delResult = jedis.del(new String[] { key });
            return delResult;
        } catch(Exception e) {
            e.printStackTrace();
            pool.returnResource(jedis);
            throw new JedisException(e.getMessage());
        }finally{
        	pool.returnResource(jedis);
        }
    }
    
    /**
     * 删除成功返回1，存在异常返回-1
     * @param key
     * @param fields
     * @return
     */
    public long remove(String key, String[] fields) {
        Jedis jedis = null;
        try {
        	jedis = pool.getResource();
       		return jedis.hdel( key, fields );
        } catch (Exception e) {
        	 e.printStackTrace();
        	 pool.returnBrokenResource(jedis);
         	 throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    
    /**
     * 设置过期时间 (秒)
     * @param key
     * @param seconds
     * @return
     */
    public long setTTLSeconds( String key, int seconds ) {
        Jedis jedis = null;
        try {
        	jedis = pool.getResource();
        	if( seconds < 0)
        		return jedis.persist( key );
            return jedis.expire( key, seconds );
        } catch (Exception e) {
        	e.printStackTrace();
        	pool.returnBrokenResource(jedis);
        	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }
    

//    public static void main(String[] args) {
//    	JedisPoolConfig config = new JedisPoolConfig();
//    	config.setMaxActive(100);
//    	config.setMaxIdle(20);
//    	config.setMaxWait(1000l);
//    	
//    	
//    	JedisPool pool = new JedisPool(config, "127.0.0.1", 6379);
//    	JedisHashCache jedisHashCache = new JedisHashCache(pool, "hashCaheTest");
//    	jedisHashCache.setTTLSeconds("hashCaheTest", 6);
//    	jedisHashCache.setValue("tes", "啊实打实", false);
//    	
//    System.err.println(jedisHashCache.getValue( "hashCaheTest","tes"));	;
////    	List<Object> resultList = new ArrayList<Object>();
////    	Map<String,Object> resultMap = new HashMap<String,Object>();
////    	List<String> paramList = new ArrayList<String>();
////    	paramList.add("8001");
////    	resultMap = (Map<String, Object>) jedisHashCache.hgetAll("QUEUE_CENTRAL");
////    	long map = jedisHashCache.size("QUEUE_CENTRAL");
////    	resultMap.get("BMS_ORDER100001");
////    	System.out.println("resultMap="+ resultMap);
////    	Map<String,Object> bmsMap = (Map<String, Object>) resultMap.get("BMS_ORDER100001");
////    	String bmsMap = (String) resultMap.get("BMS_ORDER100001");
////    	System.out.println("resultList="+bmsMap);
////    	String a = "";
////    	if(!StringUtils.isEmpty(a)){
////    		System.out.println("a"+a);
////    	}
//    	long memSize = jedisHashCache.memSize();
//    	System.out.println("memSize="+memSize);
//    	
//	}
    
    
}


