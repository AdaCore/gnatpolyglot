import com.adacore.gnatpolyglot.runtime.BooleanRef;
import com.adacore.gnatpolyglot.runtime.CharacterRef;
import com.adacore.gnatpolyglot.runtime.IntegerRef;
import com.adacore.libtest.Callbacks;
import com.adacore.libtest.test.Enum;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {

        Callbacks.Callback0 p = new Callbacks.Callback0() {

            public void accept
              (IntegerRef x, CharacterRef y, Enum.Ref z, BooleanRef t) {

                System.out.println
                  ("From Java, {X, Y, Z, T}={" + x + ", " + y + ", "
                   + z + ", " + t + "}");

                x.setValue(42);
                y.setValue('z');
                z.setValue(Enum.C);
                t.setValue(false);
            }};

        TestPackage.callProc(p);
    }
}
