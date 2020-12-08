import com.jmall.common.RedisClusterPool;
import org.junit.Test;
import redis.clients.jedis.JedisCluster;

public class ReidsClutserTest {


    @Test
    public void clusterSet() {
        JedisCluster jedisCluster = RedisClusterPool.getJedisCluster();
        for (int i = 0; i <100 ; i++) {
            jedisCluster.set("key"+i,"value"+i);
            System.out.println("program is end");
        }
    }
}
