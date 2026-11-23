import com.adacore.libtest.test.TestPackage;
import com.adacore.gnatpolyglot.runtime.PolyglotException;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("start...");
            TestPackage.raiseSomething();
        } catch (PolyglotException e) {
            System.out.println("Caught regular exception");
        } finally {
            System.out.println("finished");
        }
    }
}
