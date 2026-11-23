import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.R;

public class Main {
    public static void main(String[] args) {
        R v = TestPackage.f().get();
        TestPackage.p(v);
        v = TestPackage.getX().get();
        TestPackage.p(v);
    }
}
