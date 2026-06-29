import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.T;
import com.adacore.libtest.rec1.R1;
import com.adacore.libtest.rec2.R2;
import com.adacore.libtest.rec3.R3;
import com.adacore.libtest.rec4.R4;

public class Main {

    public static void main(String[] args) throws Throwable {
        T value = new T(new R1());
        System.out.println(value.getA().getI());
        R2 r2 = new R2();
        TestPackage.p1(r2);
        R3.Array arr = new R3.Array(1, 3);
        TestPackage.p2(arr);

        TestPackage.p3(new com.adacore.libtest.renamed.T());

        R4 r4 = new R4();
        r4._setOwner(Owner.LIBRARY);
        TestPackage.p4(r4);
        r4._setOwner(Owner.USER);

    }
}
