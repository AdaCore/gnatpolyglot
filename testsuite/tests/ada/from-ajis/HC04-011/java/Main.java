import com.adacore.libtest.Callbacks;
import com.adacore.libtest.test.Enum1;
import com.adacore.libtest.test.Enum2;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {

        TestPackage.proc(Enum1.A);

        Enum2.Ref ref = new Enum2.Ref(Enum2.A);

        ref.setValue(Enum2.B);

        TestPackage.proc2(ref);

        System.out.println(ref.getValue());

        System.out.println(TestPackage.get());

        TestPackage.call(new Callbacks.Callback0() {

            @Override
            public void accept(Enum2 v1, Enum2.Ref v2, Enum2.Ref v3) {
                System.out.println("V1, Java callback: " + v1);
                System.out.println("V3, Java callback: " + v3.getValue());

                v2.setValue(Enum2.E);
                v3.setValue(Enum2.F);
            }});
    }
}
