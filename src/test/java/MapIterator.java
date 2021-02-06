import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

public class MapIterator {

    @Test
    public void iterator1 (){
        Map<Integer,Integer> map1 = Maps.newHashMap();
        map1.put(1,1);
        map1.put(2,2);
        map1.put(3,3);

        for (Map.Entry<Integer,Integer> entry : map1.entrySet()) {
            System.out.println("key="+entry.getKey()+" value="+entry.getValue());
        }
    }

    @Test
    public void iterator2 (){
        Map<Integer,Integer> map1 = Maps.newHashMap();
        map1.put(1,1);
        map1.put(2,2);
        map1.put(3,3);

        for (Integer key : map1.keySet()) {
            System.out.println("key="+key);
        }
        for (Integer value : map1.values()) {
            System.out.println("value="+value);
        }
    }

    @Test
    public void iterator3 (){
        Map<Integer,Integer> map1 = Maps.newHashMap();
        map1.put(1,1);
        map1.put(2,2);
        map1.put(3,3);

        Iterator<Map.Entry<Integer,Integer>> entries = map1.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry<Integer,Integer> entry = entries.next();
            System.out.println("key="+entry.getKey()+" value="+entry.getValue());
        }

        Iterator its = map1.entrySet().iterator();
        while (its.hasNext()) {
            Map.Entry entry = (Map.Entry) its.next();
            Integer key = (Integer) entry.getKey();
            Integer value = (Integer) entry.getValue();
        }
    }
}
