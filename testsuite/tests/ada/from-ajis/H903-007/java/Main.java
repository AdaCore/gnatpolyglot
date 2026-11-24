import com.adacore.libtest.p_a.AType;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        AType a = TestPackage.f((byte)5);
        System.out.println("Object created");
    }
}
