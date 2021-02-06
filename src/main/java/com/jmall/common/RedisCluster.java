package com.jmall.common;

import com.jmall.util.PropertiesUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import java.util.HashSet;
import java.util.Set;

/**
 * 单例模式jedisCluster
 * 测试成功，可连接到redis集群
 * UserReidsClusterController测试
 */
public class RedisCluster {

    private static JedisCluster jedisCluster = null;

    private static final Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.maxTotal","20")); //最大连接数
    private static final Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.maxIdle","10"));//在jedispool中最大的idle状态(空闲的)的jedis实例的个数
    private static final Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.minIdle","2"));//在jedispool中最小的idle状态(空闲的)的jedis实例的个数
    private static final Integer maxWaitMillis = Integer.parseInt(PropertiesUtil.getProperty("redis.maxWaitMills","1000"));
    private static final Integer minEvictableIdleTimeMillis = Integer.parseInt(PropertiesUtil.getProperty("redis.minEvictableIdleTimeMillis","6000"));
    private static final Integer timeBetweenEvictionRunsMillis = Integer.parseInt(PropertiesUtil.getProperty("redis.timeBetweenEvictionRunsMillis","30000"));
    private static final Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.testOnBorrow","true"));//在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static final Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.testOnReturn","true"));//在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。

    private static final String redisClusterIp = PropertiesUtil.getProperty("redis.cluster.ip");
    private static final Integer redisCluster1Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port1","7000"));
    private static final Integer redisCluster2Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port2","7001"));
    private static final Integer redisCluster3Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port3","7002"));
    private static final Integer redisCluster4Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port4","7003"));
    private static final Integer redisCluster5Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port5","7004"));
    private static final Integer redisCluster6Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port6","7005"));

    public synchronized static JedisCluster getJedisCluster() {

        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
        // Could not get a resource from the pool
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster1Port));
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster2Port));
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster3Port));
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster4Port));
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster5Port));
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster6Port));

        // 只有当jedisCluster为空时才实例化
        if (jedisCluster == null) {
            jedisCluster = new JedisCluster(jedisClusterNodes, config);
        }
        return jedisCluster;
    }

    public static void main(String[] args) {
        JedisCluster jedisCluster = getJedisCluster();
        for (int i = 0; i <100 ; i++) {
            jedisCluster.set("key"+i,"value"+i);
        }
        System.out.println("program is end");
    }
}
