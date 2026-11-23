import com.adacore.libtest.test.TestPackage;
import com.adacore.gnatpolyglot.runtime.IntegerRef;
import com.adacore.gnatpolyglot.runtime.LongRef;

public class Main {
    public static void main(String[] args) {
        int v1 = TestPackage.f1();
        long v2 = TestPackage.f2();
        System.out.println(v1);
        System.out.println(v2);
        IntegerRef r1 = new IntegerRef(v1);
        LongRef r2 = new LongRef(v2);
        TestPackage.minusOneInt(r1);
        TestPackage.minusOneLong(r2);
        System.out.println(r1.getValue());
        System.out.println(r2.getValue());
    }
}
