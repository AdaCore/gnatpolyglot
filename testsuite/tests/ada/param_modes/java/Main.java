import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.T;
import com.adacore.gnatpolyglot.runtime.IntegerRef;

public class Main {
    public static void main(String[] args) {
        IntegerRef a = new IntegerRef(-1);
        T b = new T();

        System.out.println("Calling inProc");
        TestPackage.inProc(a.getValue(), b);
        System.out.println("Calling outProc");
        TestPackage.outProc(a, b);
        System.out.println("Calling inProc");
        TestPackage.inProc(a.getValue(), b);
        System.out.println("Calling inOutProc");
        TestPackage.inOutProc(a, b);
        System.out.println("Calling inProc");
        TestPackage.inProc(a.getValue(), b);
    }
}
