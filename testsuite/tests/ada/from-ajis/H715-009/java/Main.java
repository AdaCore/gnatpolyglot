import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.T;
import com.adacore.libtest.test.TestPackage;

public class Main {

    static class TChild extends T {
        TChild(int f) {
            super(f);
        }

        //  Stand-in for AJIS' IProxy.proxyCloned: both the Ada-side copy and
        //  the Java-side clone go through Object.clone.
        @Override
        public Object clone() throws CloneNotSupportedException {
            System.out.println("PROXY CLONED !");
            return super.clone();
        }
    }

    public static void main(String[] args) {
        // These two test perform a copy from Ada, and then from Java. On the
        // two case, the Java & the Ada objects should be different, but the
        // class should be the same.
        T v = new TChild(0);
        v.setF(777);
        //  Copy takes an access value: the library may keep it.
        v._setOwner(Owner.LIBRARY);
        System.out.println("BEFORE COPY");
        T w = TestPackage.copy(v).get();
        System.out.println("AFTER COPY");
        w.setF(888);
        System.out.println(v == w);
        System.out.println(w.getClass());
        System.out.println(v.getF() + ", " + w.getF());
        try {
            System.out.println("BEFORE COPY");
            w = (T) v.clone();
            System.out.println("AFTER COPY");
        } catch (CloneNotSupportedException e) {
            System.out.println("GOT EXCEPTION");
        }
        w.setF(999);
        System.out.println(v == w);
        System.out.println(w.getClass());
        System.out.println(v.getF() + ", " + w.getF());
    }
}
