package com.framework.loippi.utils.jedisUtil;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;


public class JedisStringCache extends JedisCache  {
	
	private String key;
	private int seconds=60*60*24*30;//30天
	
	public JedisStringCache(JedisPool pool, String key ){
		super( pool );
		this.key = key;
	}
	public JedisStringCache( JedisPool pool){
		super( pool );
	}
	
    public long set(String value,int seconds ) {
    	return set( value,  seconds, false );
    }
    
    public long set(String value ) {
    	return set( value,  seconds, false );
    }

    /**
     * 
     * @param key
     * @param value
     * @param forceNewNotExist
     * @return 1-设置成功 ;0-值已存在
     */
    public long set(String value,int seconds , boolean forceNewNotExist) {
        Jedis jedis = null;
        try {
        	jedis = pool.getResource();
        	if( forceNewNotExist )
        		return jedis.setnx( key , value);
            jedis.set( key , value);
            if( seconds < 0){
        		return jedis.persist( key );
            }
            jedis.expire( key, seconds );
            return 1L;
        } catch (Exception e) {
            e.printStackTrace();
        	pool.returnBrokenResource(jedis);
         	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    public String get(){
    	Jedis jedis = null;
    	try {
        	jedis = pool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        	pool.returnBrokenResource(jedis);
         	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }
    


    public long incr(String key, long value) {
        Jedis jedis = null;
        try {
        	jedis = pool.getResource();
    		return jedis.incrBy(key, value);
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
     * 设置过期时间 (秒)
     * @param key
     * @param seconds
     * @return
     */
    public long setTTLSeconds(int seconds ) {
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
    
    
    public void addList(String listName,String value,int seconds){
    	Jedis jedis = null;
    	try {
        	jedis = pool.getResource();
        	jedis.lpush(listName,  value);
        	jedis.expire( listName, seconds );
        } catch (Exception e) {
            e.printStackTrace();
        	pool.returnBrokenResource(jedis);
         	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    }
    

    
    public List<String> getList(String listName,int num1,int num2){
    	Jedis jedis = null;
    	List<String> list=new ArrayList();
    	try {
        	jedis = pool.getResource();
        	return  jedis.lrange(listName, num1, num2);
        } catch (Exception e) {
            e.printStackTrace();
        	pool.returnBrokenResource(jedis);
         	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
    	
    }
    

    
//	-- 函数：尝试获得红包，如果成功，则返回json字符串，如果不成功，则返回空
//	-- 参数：红包队列名， 已消费的队列名，去重的Map名，用户ID
//	-- 返回值：nil 或者 json字符串，包含用户ID：userId，红包ID：id，红包金额：money
	static String tryGetHongBaoScript = 
//			"local bConsumed = redis.call('hexists', KEYS[3], KEYS[4]);\n"
//			+ "print('bConsumed:' ,bConsumed);\n"
			"if redis.call('hexists', KEYS[3], KEYS[4]) ~= 0 then\n"
			+ "return nil\n"
			+ "else\n"
			+ "local hongBao = redis.call('rpop', KEYS[1]);\n"
//			+ "print('hongBao:', hongBao);\n"
			+ "if hongBao then\n"
			+ "local x = cjson.decode(hongBao);\n"
			+ "x['userId'] = KEYS[4];\n"
			 + "x['userId'] = KEYS[4];\n"  
	            + "x['userName'] = KEYS[5];\n"  
	            + "x['userAvatar'] = KEYS[6];\n"  
	            + "x['userTime'] = KEYS[7];\n"  
			+ "local re = cjson.encode(x);\n"
			+ "redis.call('hset', KEYS[3], KEYS[4], KEYS[4]);\n"
			+ "redis.call('lpush', KEYS[2], re);\n"
			+ "return re;\n"
			+ "end\n"
			+ "end\n"
			+ "return nil";
    
//	参数说明：
//	script： 参数是一段 Lua 5.1 脚本程序。脚本不必(也不应该)定义为一个 Lua 函数。
//	numkeys： 用于指定键名参数的个数。
//	key [key ...]： 从 EVAL 的第三个参数开始算起，表示在脚本中所用到的那些 Redis 键(key)，这些键名参数可以在 Lua 中通过全局变量 KEYS 数组，用 1 为基址的形式访问( KEYS[1] ， KEYS[2] ，以此类推)。
//	arg [arg ...]： 附加参数，在 Lua 中通过全局变量 ARGV 数组访问，访问的形式和 KEYS 变量类似( ARGV[1] 、 ARGV[2] ，诸如此类)。
    /**
     * 
     * @param hongBaoList  红包队列名
     * @param hongBaoConsumedList 已消费的队列名
     * @param hongBaoConsumedMap 去重的Map名
     * @param userId 抢红包用户ID
     * @param userName 抢红包用户名
     * @param userAvatar 抢红包用户头像
     * @param time 抢红包时间
     * @return  抢红包结果   {"userName":"name7","userAvatar":"avatar7","id":8,"userTime":"1491878806265","userId":"7","money":8}
     */
	

    public  String tryGetHongBaoList(String hongBaoList,String hongBaoConsumedList,String  hongBaoConsumedMap,String userId
    		,String userName,String userAvatar,long time){
    	Jedis jedis = null;
    	try {
         jedis = pool.getResource();
    	Object object = jedis.eval(tryGetHongBaoScript, 7, hongBaoList, hongBaoConsumedList,
    			hongBaoConsumedMap, userId, userName,userAvatar, "" +time);
		
		if (object != null) {
			System.out.println(" get hongBao:" + object.toString());
			return object.toString();
		}else {
			//已经取完了
			if(jedis.llen(hongBaoList) == 0){
				return "0";
			}
			
		}
    	}catch (Exception e) {
            e.printStackTrace();
        	pool.returnBrokenResource(jedis);
         	throw new JedisException(e.getMessage());
        } finally {
            pool.returnResource(jedis);
        }
		return "-1";
    }
    
    
    
//
//    public static void main(String[] args) {
//    	JedisPoolConfig config = new JedisPoolConfig();
//    	config.setMaxActive(100);
//   
//    	
//    	config.setMaxIdle(20);
//    	config.setMaxWait(1000l);
//    	
//
//    	JedisPool pool = new JedisPool(config, "127.0.0.1");
//  
//    	JedisStringCache jedisStringCache = new JedisStringCache(pool, "list");
//
//    	jedisStringCache.addList("list", "asdasd", 10000);
//    
//    //	jedisStringCache.set("tesa阿斯顿sdaasd",6,false);
//    	
//
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
//    
//    	
//    	
//	}
}








