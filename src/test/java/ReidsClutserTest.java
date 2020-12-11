import com.jmall.common.RedisCluster;
import org.junit.Test;
import redis.clients.jedis.JedisCluster;

public class ReidsClutserTest {

    @Test
    public void clusterSet() {
        JedisCluster jedisCluster = RedisCluster.getJedisCluster();
        for (int i = 0; i <100 ; i++) {
            jedisCluster.setex("key"+i,200,"value"+i);
        }
            System.out.println("program is end");
    }
}
