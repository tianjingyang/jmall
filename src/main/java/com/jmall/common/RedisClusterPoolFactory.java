package com.jmall.common;

import com.jmall.util.PropertiesUtil;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import java.util.HashSet;
import java.util.Set;

public class RedisClusterPoolFactory implements PooledObjectFactory<JedisCluster> {

    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));//在jedispool中最大的idle状态(空闲的)的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2"));//在jedispool中最小的idle状态(空闲的)的jedis实例的个数

    private static String redisClusterIp = PropertiesUtil.getProperty("redis.cluster.ip");
    private static Integer redisCluster1Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port1"));
    private static Integer redisCluster2Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port2"));
    private static Integer redisCluster3Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port3"));
    private static Integer redisCluster4Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port4"));
    private static Integer redisCluster5Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port5"));
    private static Integer redisCluster6Port = Integer.parseInt(PropertiesUtil.getProperty("redis.cluster.port6"));
    @Override
    public PooledObject<JedisCluster> makeObject() throws Exception {
        System.out.println("make Object");
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。

        // 最大允许等待时间，如果超过这个时间还未获取到连接，则会报JedisException异常：
        // Could not get a resource from the pool
        config.setMaxWaitMillis(1000);

        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();

        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster1Port));
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster2Port));
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster3Port));
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster4Port));
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster5Port));
        jedisClusterNodes.add(new HostAndPort(redisClusterIp, redisCluster6Port));

        JedisCluster JedisCluster = new JedisCluster(jedisClusterNodes,config);

        return new DefaultPooledObject<>(JedisCluster);
    }

    public void destroyObject(PooledObject<JedisCluster> arg0) throws Exception {
        System.out.println("destroy Object");
        JedisCluster JedisCluster = arg0.getObject();
        JedisCluster = null;
    }

    /**
     * 功能描述：判断资源对象是否有效，有效返回 true，无效返回 false
     *
     * 什么时候会调用此方法
     * 1：从资源池中获取资源的时候，参数 testOnBorrow 或者 testOnCreate 中有一个 配置 为 true 时，则调用  factory.validateObject() 方法
     * 2：将资源返还给资源池的时候，参数 testOnReturn，配置为 true 时，调用此方法
     * 3：资源回收线程，回收资源的时候，参数 testWhileIdle，配置为 true 时，调用此方法
     */
    public boolean validateObject(PooledObject<JedisCluster> arg0) {
        System.out.println("validate Object");
        return true;
    }


    /**
     * 功能描述：激活资源对象
     *
     * 什么时候会调用此方法
     * 1：从资源池中获取资源的时候
     * 2：资源回收线程，回收资源的时候，根据配置的 testWhileIdle 参数，
     * 判断 是否执行 factory.activateObject()方法，true 执行，false 不执行
     * @param arg0
     */
    public void activateObject(PooledObject<JedisCluster> arg0) throws Exception {
        System.out.println("activate Object");
    }

    /**
     * 功能描述：钝化资源对象
     *
     * 什么时候会调用此方法
     * 1：将资源返还给资源池时，调用此方法。
     */
    public void passivateObject(PooledObject<JedisCluster> arg0) throws Exception {
        System.out.println("passivate Object");
    }
}
