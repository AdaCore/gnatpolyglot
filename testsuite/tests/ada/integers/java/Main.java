import com.adacore.libtest.ints.IntsPackage;
import com.adacore.gnatpolyglot.runtime.ada2java.ConstraintError;

public class Main {
    public static void main(String[] args) {
        assert IntsPackage.fChar() == 80;
        assert IntsPackage.fShort() == Short.MAX_VALUE;
        assert IntsPackage.fInt() == Integer.MAX_VALUE;
        if (System.getProperty("os.name").startsWith("Windows")) {
            assert IntsPackage.fLongInt() == Integer.MAX_VALUE;
        } else {
            assert IntsPackage.fLongInt() == Long.MAX_VALUE;
        }

        assert IntsPackage.fMyInt() == (1 << 20);
        assert IntsPackage.fMyNewShort() == 1000;
        if (System.getProperty("os.name").startsWith("Windows")) {
            assert IntsPackage.fMyLongInt() == Integer.MIN_VALUE;
        } else {
            assert IntsPackage.fMyLongInt() == Long.MIN_VALUE;
        }
        assert IntsPackage.fMySmall() == -256;

        assert IntsPackage.fPositive() == 1;
        assert IntsPackage.fMyPositive() == 2;

        try {
            IntsPackage.p((short) 100);
            assert false;
        } catch (ConstraintError e) {
            // Avoid printing the exception message: it would contain a sloc from
            // generated glue code which could change at any moment.
        }

    }
}
