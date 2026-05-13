import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Exc1;
import com.adacore.libtest.test.Exc2;
import com.adacore.gnatpolyglot.runtime.PolyglotException;

public class Main {

    public static void main(String[] args) throws Throwable {
        try {
            TestPackage.getException(1);
        } catch (Exc1 e) {
            System.out.println("caught" + e.getMessage());
        }
        try {
            TestPackage.getException(2);
        } catch (Exc2 e) {
            System.out.println("caught" + e.getMessage());
        }
        try {
            TestPackage.getException(3);
        } catch (PolyglotException e) {
            System.out.println("caught" + e.getMessage());
        }
        try {
            TestPackage.getException(0);
        } catch (com.adacore.libtest.other.Exc1 e) {
            System.out.println("caught" + e.getMessage());
        }

        // should not throw: verify that the exception was cleared.
        TestPackage.getException(4);
    }
}
