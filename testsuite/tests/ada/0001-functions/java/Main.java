import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        System.out.format("Got %s from Ada.\n", TestPackage.f());
        System.out.format("Got %s from Ada.\n", TestPackage.fExpr());
    }
}
