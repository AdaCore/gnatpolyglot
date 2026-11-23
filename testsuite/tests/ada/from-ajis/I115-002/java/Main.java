import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.R;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {
        R r = new R(0, 0);

        r.setA(1);
        r.setB(2);

        r._setOwner(Owner.LIBRARY);

        r = TestPackage.f2(r).get();

        System.out.println(r.getA());
        System.out.println(r.getB());

    }
}
