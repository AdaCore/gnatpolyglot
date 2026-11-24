import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        System.out.println(TestPackage.getV1() + ", " + TestPackage.getV2());
        TestPackage.setV1(999);
        TestPackage.setV2(new PolyglotString("9876543210"));
        System.out.println(TestPackage.getV1() + ", " + TestPackage.getV2());
        TestPackage.setV3(new PolyglotString("This is the rythm of the life, yeah !"));
        System.out.println(TestPackage.getV3().get());
    }
}
