import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.Rec;
import com.adacore.libtest.test.TestPackage;

public class Main {

    static class ChildRec extends Rec {
        public int p;

        public ChildRec() {
            p = 999;
        }
    }

    public static void main(String[] args) {
        ChildRec v1 = new ChildRec();

        v1.p = 5;

        // Get_Same_Rec takes an access value: the library needs ownership.
        v1._setOwner(Owner.LIBRARY);

        Rec v2 = TestPackage.getSameRec(true, v1).get();
        v2 = TestPackage.getSameRec(true, v1).get();
        v2 = TestPackage.getSameRec(true, v1).get();
        v2 = TestPackage.getSameRec(true, v1).get();

        if (v2 instanceof ChildRec) {
            if (v1 == v2) {
                System.out.println("references are the same");
            }

            System.out.println("extracted P is " + ((ChildRec) v2).p);
        } else {
            System.out.println("the same object is NO v2");
        }
    }
}
