import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.C;
import com.adacore.libtest.test.C2;
import com.adacore.libtest.test.TestPackage;

public class Main {

    static class CChild extends C {
        public CChild() {
            System.out.println("CREATE JAVA C");
        }
    }

    public static void main(String[] args) {
        C c = new C();
        c._setOwner(Owner.LIBRARY);
        TestPackage.call(c);

        C2 c2 = new C2(true);
        c2._setOwner(Owner.LIBRARY);
        TestPackage.call2(c2);

        CChild cChild = new CChild();
        cChild._setOwner(Owner.LIBRARY);
        TestPackage.call(cChild);
    }
}
