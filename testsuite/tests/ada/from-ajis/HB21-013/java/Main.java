import com.adacore.libtest.test.ATaggedType;
import com.adacore.libtest.test.TestPackage;

public class Main {
    static class Child extends ATaggedType {}

    public static void main(String[] args) {
        ATaggedType v1 = TestPackage.sth();
        ATaggedType v2 = v1.prim();
        System.out.println(v1.getV());
        v2.setV(999);
        System.out.println(v1.getV());
        System.out.println(v2.getV());

        Child c = new Child();
        ATaggedType v3 = TestPackage.callPrim(c);
        System.out.println(c.getV());
        v3.setV(998);
        System.out.println(c.getV());
        System.out.println(v3.getV());
    }
}
