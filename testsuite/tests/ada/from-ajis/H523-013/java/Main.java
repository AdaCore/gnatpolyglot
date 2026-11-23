import com.adacore.libtest.test.T;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] args) {
        T v1 = new T(0);
        T v2 = new T(0);
        v1.setV(1);
        v2.setV(2);

        T v3 = TestPackage.getTChild(1).get();
        T v4 = TestPackage.getTChild(2).get();

        T r1 = v1.f(v2);
        T r2 = v3.f(v4);

        System.out.println("r1 = " + r1.getV());
        System.out.println("r2 = " + r2.getV());
    }
}
