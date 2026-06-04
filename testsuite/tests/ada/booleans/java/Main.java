import com.adacore.libtest.test.TestPackage;
import com.adacore.gnatpolyglot.runtime.BooleanRef;

public class Main {

    public static void main(String[] args) throws Throwable {
        System.out.println(TestPackage.isEven(1));
        System.out.println(TestPackage.isEven(2));

        BooleanRef isOdd = new BooleanRef(false);
        TestPackage.isOdd(isOdd, 1);
        System.out.println(isOdd);
        TestPackage.isOdd(isOdd, 2);
        System.out.println(isOdd);

        TestPackage.printBool(true);
        TestPackage.printBool(false);
    }
}
