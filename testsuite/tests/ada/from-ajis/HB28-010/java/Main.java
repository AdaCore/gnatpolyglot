import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {

        com.adacore.libtest.a.AType a = TestPackage.getV1();
        com.adacore.libtest.b.AType b = TestPackage.getV2();

        System.out.println(a.getV());
        System.out.println(b.getV());

    }
}
