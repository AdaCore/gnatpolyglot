import com.adacore.gnatpolyglot.runtime.BooleanRef;
import com.adacore.gnatpolyglot.runtime.CharacterRef;
import com.adacore.gnatpolyglot.runtime.FloatRef;
import com.adacore.gnatpolyglot.runtime.LongRef;
import com.adacore.libtest.Callbacks;
import com.adacore.libtest.test.AnEnum;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {

        TestPackage.callACallback(new Callbacks.Callback0() {

            @Override
            public void accept(LongRef v1, LongRef v2, FloatRef v3,
                    FloatRef v4, BooleanRef v5, BooleanRef v6, AnEnum.Ref v7,
                    AnEnum.Ref v8, CharacterRef v9, CharacterRef v10) {

                // AJIS was run with --no-java-enum: enums are seen as their
                // position number.
                System.out.println(v2.getValue() + ", " + v4.getValue() + ", "
                        + v6.getValue() + ", " + v8.getValue().value + ", "
                        + v10.getValue());

                v1.setValue(987);
                v2.setValue(654);
                v3.setValue(40.0f);
                v4.setValue(42.0f);
                v5.setValue(false);
                v6.setValue(true);
                v7.setValue(AnEnum.A);
                v8.setValue(AnEnum.C);
                v9.setValue('w');
                v10.setValue('e');
            }
        });

    }

}
