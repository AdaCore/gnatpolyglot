import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.T;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static class TChild extends T {
    }

    public static void main(String[] args) {
        T v = new TChild();
        //  Set stores V in a library-level variable: hand its ownership to
        //  the library.
        v._setOwner(Owner.LIBRARY);
        TestPackage.set(true, v);
        T w = TestPackage.getCopy().get();
        T x = TestPackage.getReference().get();

        v.setF(5);
        w.setF(6);
        x.setF(7);

        System.out.println((v == w));
        System.out.println((v == x));
        System.out.println(v.getF());
        System.out.println(w.getF());
        System.out.println(x.getF());
    }
}
