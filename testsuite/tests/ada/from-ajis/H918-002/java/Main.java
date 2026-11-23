import java.util.Optional;

import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;
import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {
        IntegerArray a;

        a = TestPackage.getAnArray().get();

        System.out.println(a.getUnslided(3));

        Root r = new Root();

        Root r2 = new Root() {
            @Override
            public Optional<IntegerArray> getAnArray() {
                IntegerArray a = new IntegerArray(1, 10);

                for (int j = 1; j <= 10; ++j) {
                    a.setUnslided(j, 43);
                }

                return Optional.of(a);
            }
        };

        a = r.getAnArray().get();

        System.out.println(a.getUnslided(3));

        a = r2.getAnArray().get();

        System.out.println(a.getUnslided(3));
    }
}
