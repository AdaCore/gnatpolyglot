import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.TestPackage;
import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;
import com.adacore.libtest.test.R;

public class Main {

    public static void main(String[] argv) {
        // Global V = (1 => 1, 2 => 2): print via value
        TestPackage.print1(TestPackage.getV());

        // Global V2 = (1 => 3, 2 => 4): print via access
        TestPackage.print2(TestPackage.getV2().get());
        TestPackage.print3(TestPackage.getV2().get());

        // Record R: F = (1 => 5, 2 => 6), F2 = (1 => 7, 2 => 8)
        R r = new R();
        TestPackage.print1(r.getF());
        TestPackage.print2(r.getF2().get());
        TestPackage.print3(r.getF2().get());

        // Array created on the Java side
        IntegerArray a = new IntegerArray(1, 2);
        a.setUnslided(1, 9);
        a.setUnslided(2, 10);
        TestPackage.print1(a);
        // Access parameters require memory that may escape to Ada.
        a._setOwner(Owner.LIBRARY);
        try {
            TestPackage.print2(a);
            TestPackage.print3(a);
        } finally {
            // Neither procedure retains the array. Restore Java ownership.
            a._setOwner(Owner.USER);
        }
    }

}
