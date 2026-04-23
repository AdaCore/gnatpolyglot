import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        TestPackage.pParam(42);
        System.out.format("Got %s from Ada.\n", TestPackage.fParam(2, 3, 4));
    }
}
