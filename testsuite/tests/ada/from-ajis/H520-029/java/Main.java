import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.R1;
import com.adacore.libtest.test.R2;

public class Main {
    public static void main(String[] args) {
        R1 v1 = new R1(0);
        R2 v2 = new R2(0);

        v1.setV(99);
        v2.setV(88);

        TestPackage.print
          (TestPackage.getStdOutput(),
           new PolyglotString(v1.getV() + ", " + v2.getV()));
    }
}
