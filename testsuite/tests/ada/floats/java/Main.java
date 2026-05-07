import com.adacore.libtest.floats.FloatsPackage;

public class Main {
    public static void main(String[] args) {
        assert FloatsPackage.fShort() == 0.5f;
        assert FloatsPackage.fFloat() == 0.25f;
        assert FloatsPackage.fLongFloat() == 0.125f;
        assert FloatsPackage.fMyFloat() == 10.025f;
        assert FloatsPackage.fMyNewShort() == 0.123;
        assert FloatsPackage.fMyLongFloat() == 1000.25;
        assert FloatsPackage.fMySmall() == -0.123;
        assert FloatsPackage.inc(4.5f) == 5.5f;
        assert FloatsPackage.identity(-0.0f) == -0.0f;
        assert FloatsPackage.identity(Float.POSITIVE_INFINITY) == Float.POSITIVE_INFINITY;
        assert FloatsPackage.identity(Float.NaN) == Float.NaN;
    }
}
