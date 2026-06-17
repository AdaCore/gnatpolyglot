import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.T;

public class Main {

    public static void main(String[] args) throws Throwable {
        T a = TestPackage.toT(3);
        T b = TestPackage.toT(4);
        a.print();
        b.print();

        a.addT(b).print();
    }
}
