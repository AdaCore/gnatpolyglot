import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Enum;

public class Main {
    public static void main(String[] args) {
        System.out.println(TestPackage.isA(Enum.A));
        System.out.println(TestPackage.isA(Enum.B));
    }
}
