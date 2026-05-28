import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Enum1;
import com.adacore.libtest.test.Enum2;
import com.adacore.libtest.test.Enum3;
import com.adacore.libtest.test.Enum4;
import com.adacore.libtest.test.R;
import com.adacore.libtest.test.child.ChildPackage;
import com.adacore.libtest.test.child.E;

public class Main {

    public static void main(String[] args) throws Throwable {
        Enum1 e1 = TestPackage.fEnum(Enum1.A);
        System.out.println("Java got: " + e1.value);
        Enum2 e2 = TestPackage.fEnum(Enum2.B);
        System.out.println("Java got: " + e2.value);

        for (var v : Enum1.values()) {
            if (!Enum2.fromValue.containsKey(v.value)) {
                System.out.println(v + " is not in Enum2");
            }
        }
        System.out.println();

        for (var v : Enum1.values()) {
            if (!Enum3.fromValue.containsKey(v.value)) {
                System.out.println(v + " is not in Enum3");
            }
        }
        System.out.println();

        for (var v : Enum1.values()) {
            if (!Enum4.fromValue.containsKey(v.value)) {
                System.out.println(v + " is not in Enum4");
            }
        }
        System.out.println();

        R r = new R();
        r.getEnum();
        E ce = ChildPackage.getEnum(r);
        System.out.println(ce);
    }
}
