import org.junit.Test;

import javax.lang.model.type.TypeKind;
import java.lang.reflect.Field;

class c {
    int i;
    double d;
    Long ll;
}

public class ReflectTest {
    @Test
    public void reflectTest() throws Exception {
        for (Field f: c.class.getDeclaredFields()) {
            System.out.println(f.getType().getCanonicalName());
        }


    }
}
