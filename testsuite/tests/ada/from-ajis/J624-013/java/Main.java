import com.adacore.libtest.test.R;
import com.adacore.libtest.test.T2;
import com.adacore.libtest.test.TestPackage;

// P(V1: R; V2: T2) — proxy library compilation fails (R made abstract).
// Also tagged types (T, T2) not yet supported in Java printer.

public class Main {
    public static void main(String[] args) {
        R r = new R(876);
        T2 t2 = new T2();
        r.p(t2);
    }
}
