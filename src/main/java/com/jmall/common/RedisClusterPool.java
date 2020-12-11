package com.jmall.common;

import com.jmall.util.PropertiesUtil;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

/**
 * jedis依赖版本2.9.0！！！！！！！！！
 * 该类创建了个GenericObjectPool连接池，把JedisCluster放在里面
 */
public class RedisClusterPool {

    public static GenericObjectPool<JedisCluster> objectPool = null;

    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.maxTotal","20")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.maxIdle","10"));//在jedispool中最大的idle状态(空闲的)的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.minIdle","2"));//在jedispool中最小的idle状态(空闲的)的jedis实例的个数

    private static Integer maxWaitMillis = Integer.parseInt(PropertiesUtil.getProperty("redis.maxWaitMills","1000"));
    private static Integer minEvictableIdleTimeMillis = Integer.parseInt(PropertiesUtil.getProperty("redis.minEvictableIdleTimeMillis","6000"));
    private static Integer timeBetweenEvictionRunsMillis = Integer.parseInt(PropertiesUtil.getProperty("redis.timeBetweenEvictionRunsMillis","30000"));
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.testOnBorrow","true"));//在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.testOnReturn","true"));//在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。

    static {
        //工厂
        RedisClusterPoolFactory factory = new RedisClusterPoolFactory();
        //资源池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        //设置最大实例总数
        poolConfig.setMaxTotal(maxTotal);
        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        // 在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的；
        poolConfig.setTestOnBorrow(testOnBorrow);
        // 在还会给pool时，是否提前进行validate操作
        poolConfig.setTestOnReturn(testOnReturn);
        //如果为true，表示有一个idle object evitor线程对idle object进行扫描，如果validate失败，此object会被从pool中drop掉；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义；
        poolConfig.setTestWhileIdle(true);
        //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义；
        poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        //表示idle object evitor两次扫描之间要sleep的毫秒数
        poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        //在minEvictableIdleTimeMillis基础上，加入了至少minIdle个对象已经在pool里面了。如果为-1，evicted不会根据idle time驱逐任何对象。如果minEvictableIdleTimeMillis>0，则此项设置无意义，且只有在timeBetweenEvictionRunsMillis大于0时才有意义
        // poolConfig.setSoftMinEvictableIdleTimeMillis();

        //创建资源池
        objectPool = new GenericObjectPool<>(factory,poolConfig);
    }
    @SuppressWarnings("finally")
    public static JedisCluster getJedisCluster(){
        JedisCluster jedisCluster=null;
        try {
            jedisCluster = objectPool.borrowObject();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            return jedisCluster;
        }

    }
    public static void closeJedisCluster(JedisCluster jedisCluster ){
        if(jedisCluster!=null){
            objectPool.returnObject(jedisCluster);
        }

    }

    public static void main(String[] args) {
        JedisCluster jedisCluster = getJedisCluster();
        for (int i = 0; i <100 ; i++) {
            jedisCluster.set("key"+i,"value"+i);
        }
        System.out.println("program is end");
    }

}
