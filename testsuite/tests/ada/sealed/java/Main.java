import java.lang.reflect.Modifier;

import com.adacore.libtest.test1.A;
import com.adacore.libtest.test2.B;

public class Main {
    public static void main(String[] args) {
        assert A.class.isSealed();
        assert A.class.isAssignableFrom(B.class);
        assert (B.class.getModifiers() & Modifier.FINAL) != 0;
    }
}
