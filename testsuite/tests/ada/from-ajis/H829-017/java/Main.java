import com.adacore.libtest.test.A;
import com.adacore.libtest.test.B;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        // F1 has no default value, so the generated constructor requires it
        // (AJIS: new B ()); F2 keeps its default value.
        B v = new B(new A(0));

        v.getF2().setV(991);

        TestPackage.getG2().setV(992);

        System.out.println(v.getF2().getV());
        System.out.println(TestPackage.getG2().getV());
    }
}
