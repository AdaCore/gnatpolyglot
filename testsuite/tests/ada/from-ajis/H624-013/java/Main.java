import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.R;
import com.adacore.libtest.test.RT;

public class Main {

    public static void main(String[] args) {
        //  R has no default constructor (V has no default value)
        final R obj = new R(0);
        //  The access values are kept by Ada: hand the object to the library
        obj._setOwner(Owner.LIBRARY);
        R.Ref r = new R.Ref(null);
        TestPackage.p1(r);

        System.out.println("AFTER P1, r is " + r.get().get().getV());

        obj.setV(2007);
        r.set(obj);
        TestPackage.p2(r);
        System.out.println("AFTER P2, obj is " + obj.getV());
        System.out.println("AFTER P2, r is " + r.get().get().getV());

        TestPackage.callP((R.Ref x) -> {
            System.out.println("IN P_ACC, X is " + x.get().get().getV());
            x.set(obj);
        });

        final RT obj_t = new RT(0);
        obj_t._setOwner(Owner.LIBRARY);
        RT.Ref r_t = new RT.Ref(null);
        TestPackage.p1T(r_t);

        System.out.println("AFTER P1_T, r is " + r_t.get().get().getV());

        obj_t.setV(2007);
        r_t.set(obj_t);
        TestPackage.p2T(r_t);
        System.out.println("AFTER P2_T, obj is " + obj_t.getV());
        System.out.println("AFTER P2_T, r is " + r_t.get().get().getV());

        TestPackage.callPT((RT.Ref x) -> {
            System.out.println("IN P_T_ACC, X is " + x.get().get().getV());
            x.set(obj_t);
        });
    }

}
