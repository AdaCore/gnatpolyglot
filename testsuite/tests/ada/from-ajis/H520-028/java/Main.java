import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.R;

public class Main {
    public static void main(String[] args) {
        System.out.println("F_Integer = " + TestPackage.standardIntegerF());
        System.out.println("F_Record = " + TestPackage.testRF().getV());
    }
}
