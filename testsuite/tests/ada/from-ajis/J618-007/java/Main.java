import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.R;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] args) {
        R.Array v = new R.Array(1, 2);
        v._setOwner(Owner.LIBRARY);

        TestPackage.setV5(v);
        TestPackage.getV5().get().getUnslided(1).setF(999);
        System.out.println(TestPackage.getV5().get().getUnslided(1).getF());

        TestPackage.getV6().getUnslided(2).setF(888);
        System.out.println(TestPackage.getV6().getUnslided(2).getF());
    }
}
