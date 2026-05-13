import com.adacore.libtest.ints.IntsPackage;

public class Main {
    public static void main(String[] args) {
        assert IntsPackage.fChar() == 80;
        assert IntsPackage.fShort() == Short.MAX_VALUE;
        assert IntsPackage.fInt() == Integer.MAX_VALUE;
        assert IntsPackage.fLongInt() == Long.MAX_VALUE;

        assert IntsPackage.fMyInt() == (1 << 20);
        assert IntsPackage.fMyNewShort() == 1000;
        assert IntsPackage.fMyLongInt() == Long.MIN_VALUE;
        assert IntsPackage.fMySmall() == -256;

        assert IntsPackage.fPositive() == 1;
        assert IntsPackage.fMyPositive() == 2;

        // TODO eng/libadalang/gnatpolyglot#90: Once exceptions are supported, test that
        // passing arguments out of their expected value range raises an exception.
    }
}
