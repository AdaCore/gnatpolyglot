import com.adacore.libtest.other.OtherPackage;
import com.adacore.libtest.test.T;

public class Main {
    public static void main(String[] args) {
        T v = new T(99);
        OtherPackage.p1(v);
        // p2 not bound: anonymous access type parameter not yet supported
        OtherPackage.p3(v);
    }
}
