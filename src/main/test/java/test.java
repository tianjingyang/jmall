import com.jmall.util.PropertiesUtil;

public class test {
    public static void main(String[] args) {
        String salt = PropertiesUtil.getProperty("password.salt");
        System.out.println("salt:"+salt);
    }
}
