import com.adacore.libtest.test.Enum1;
import com.adacore.libtest.test.Enum2;
import com.adacore.libtest.test.Enum3;
import com.adacore.libtest.test.EnumBig;
import com.adacore.libtest.test.T;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static class Child extends T {
        @Override
        public Enum2 tF(com.adacore.libtest.test.Enum1 e) {
            return switch (e) {
                case Enum1.A -> Enum2.D;
                case Enum1.B -> Enum2.E;
                default -> Enum2.F;
            };
        }
    }

    public static void main(String[] args) throws Throwable {
        Enum1 e1 = Enum1.A;
        TestPackage.pEnum1(e1);
        e1 = TestPackage.fEnum1(e1);
        TestPackage.pEnum1(e1);
        System.out.println("Java value: " + e1.value);

        Enum2 e2 = Enum2.E;
        TestPackage.pEnum2(e2);
        e2 = TestPackage.fEnum2(e2);
        TestPackage.pEnum2(e2);
        System.out.println("Java value: " + e2.value);

        Enum2.Ref e2Ref = new Enum2.Ref(e2);
        TestPackage.pInOut(e2Ref);

        System.out.println("Java value: " + e2Ref.getValue().value);

        Child c = new Child();
        System.out.println(TestPackage.callTF(c, Enum1.A).value);
        System.out.println(TestPackage.callTF(c, Enum1.B).value);
        System.out.println(TestPackage.callTF(c, Enum1.C).value);

        System.out.println("Java big:" + EnumBig.I.value);
        EnumBig eb = TestPackage.fEnumBig(EnumBig.G);
        TestPackage.pEnumBig(eb);

        Enum3 e3 = Enum3.A;
        TestPackage.pEnum1(e3);

        System.out.println(c.getE().value);
        c.setE(Enum1.B);
        System.out.println(c.getE().value);
    }
}
