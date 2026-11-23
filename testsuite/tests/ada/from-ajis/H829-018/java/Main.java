import com.adacore.libtest.test.T;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] args) {
        T v = new T();
        v.p1();
        v.p2();
        TestPackage.p3(v);
        TestPackage.p4(v);
    }
}
