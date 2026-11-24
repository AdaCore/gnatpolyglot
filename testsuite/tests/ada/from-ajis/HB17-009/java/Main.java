import com.adacore.libtest.test.TestPackage;
import com.adacore.gnatpolyglot.runtime.IntegerRef;

public class Main {
    public static void main(String[] args) {
        TestPackage.call((v1, v2, v3) -> {
            System.out.println(v1 + ", " + v3.getValue());
            v2.setValue(10);
            v3.setValue(11);
        });
    }
}
