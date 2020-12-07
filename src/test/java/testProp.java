import com.jmall.util.PropertiesUtil;
import org.junit.Test;

public class testProp {

    @Test
    public void getProp() {
        String host = PropertiesUtil.getProperty("redis.ip");
        System.out.println(host);
    }
}
