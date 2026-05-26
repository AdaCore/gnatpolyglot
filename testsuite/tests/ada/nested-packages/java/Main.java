import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.inner.InnerPackage;

public class Main {

    public static void main(String[] args) throws Throwable {
        TestPackage.outerProc();
        InnerPackage.innerProc();
    }
}
