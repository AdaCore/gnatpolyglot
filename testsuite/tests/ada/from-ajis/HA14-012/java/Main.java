import com.adacore.gnatpolyglot.runtime.ada2java.ConstraintError;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Root;

public class Main {
    public static void main(String[] args) {
        try (Root r = new Root()) {
            // Get_Str returns an access-to-constant PolyglotString.
            TestPackage.print1(r.getStr().get());

            try {
                TestPackage.print2(r.getStr().get());
            } catch (ConstraintError e) {
                System.out.println("CONSTRAINT ERROR GOT");
            }

            TestPackage.print3(r.getStr().get());

            try {
                TestPackage.print4(r.getStr().get());
            } catch (ConstraintError e) {
                System.out.println("CONSTRAINT ERROR GOT");
            }
        }
    }
}
