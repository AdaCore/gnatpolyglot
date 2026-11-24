import com.adacore.gnatpolyglot.runtime.ada2java.AdaException;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        try {
            TestPackage.raiseException();
        } catch (AdaException e) {
            System.out.println("EXCEPTION GOT IN JAVA");
            System.out.println(e.getMessage());
        }

        TestPackage.callAProc(() -> {
            throw new NullPointerException();
        });
    }
}
