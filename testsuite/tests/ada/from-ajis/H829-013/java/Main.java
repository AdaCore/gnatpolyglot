import com.adacore.libtest.test.T;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        T v = new T();

        TestPackage.p(v);
        v.p1(0.0f);
        TestPackage.f();
        TestPackage.q1(1, v);
        TestPackage.q2(1, v);
    }
}
