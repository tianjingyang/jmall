package com.jmall.util;

import com.jmall.common.RedisCluster;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

@Slf4j
public class RedisClusterUtil {

    public static String set(String key,String value) {
        JedisCluster jedisCluster = null;
        String result = null;
        try {
            jedisCluster = RedisCluster.getJedisCluster();

            result = jedisCluster.set(key,value);
        } catch (Exception e) {
            log.error("set:key{},value:{} error",key,value,e);
        }
        return result;
    }

    public static String get(String key) {
        JedisCluster jedisCluster = null;
        String result = null;
        try {
            jedisCluster = RedisCluster.getJedisCluster();
            result = jedisCluster.get(key);
        } catch (Exception e) {
            log.error("get:key{} error", key, e);
        }
        return result;
    }

    public static String setEx(String key,String value,int exTime) {
        JedisCluster jedisCluster = null;
        String result = null;
        try {
            jedisCluster = RedisCluster.getJedisCluster();
            result = jedisCluster.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setEx:key{},exTime:{},value:{} error",key,exTime,value,e);
        }
        return result;
    }

    public static Long expire(String key,int exTime) {
        JedisCluster jedisCluster = null;
        Long result = null;
        try {
            jedisCluster = RedisCluster.getJedisCluster();
            result = jedisCluster.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire:key{},exTime:{} error",key,exTime,e);
        }
        return result;
    }

    public static Long del(String key) {
        JedisCluster jedisCluster = null;
        Long result = null;
        try {
            jedisCluster = RedisCluster.getJedisCluster();
            result = jedisCluster.del(key);
        } catch (Exception e) {
            log.error("delete:key{} error",key,e);
        }
        return result;
    }

    public static void main(String[] args) {
        JedisCluster jedisCluster = RedisCluster.getJedisCluster();

        RedisClusterUtil.set("keyTest","value");

        String value = RedisClusterUtil.get("keyTest");

        RedisClusterUtil.setEx("keyex","valueex",60*10);

        RedisClusterUtil.expire("keyTest",60*20);

        RedisClusterUtil.del("keyTest");

        System.out.println("end");


    }
}
