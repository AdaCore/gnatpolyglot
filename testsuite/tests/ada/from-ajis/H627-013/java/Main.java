import com.adacore.libtest.test.AType;
import com.adacore.libtest.test.TestPackage;

public class Main {
    static class JavaChild extends AType {
        @Override
        public int raiseException() {
            throw new RuntimeException("an exception...");
        }
    }

    public static void main(String[] args) {
        AType obj = new JavaChild();

        try {
            TestPackage.callAndRaise(obj);
        } catch (Throwable e) {
            System.out.println("Exception got in java... ");
        }

        try {
            TestPackage.callAndCatch(obj);
        } catch (Throwable e) {
            System.out.println("Exception got in java... ");
        }
    }
}
