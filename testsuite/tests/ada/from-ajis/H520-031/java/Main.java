import com.adacore.gnatpolyglot.runtime.BooleanRef;
import com.adacore.gnatpolyglot.runtime.FloatRef;
import com.adacore.gnatpolyglot.runtime.IntegerRef;
import com.adacore.libtest.test.Enum;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        IntegerRef p1 = new IntegerRef(0);
        BooleanRef p2 = new BooleanRef(false);
        FloatRef p3 = new FloatRef(0.0f);
        Enum.Ref p4 = new Enum.Ref(Enum.A);

        TestPackage.p1(p1, p2, p3, p4);

        System.out.println
          (p1 + ", " + p2 + ", " + p3 + ", " + p4);

        TestPackage.p2(p1, p2, p3, p4);

        System.out.println
          (p1 + ", " + p2 + ", " + p3 + ", " + p4);
    }
}
