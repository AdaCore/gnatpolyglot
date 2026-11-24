import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.child.ChildPackage;

public class Main {
    public static void main(String[] args) {
        System.out.println(TestPackage.get().get());
        System.out.println(ChildPackage.get().get());
    }
}
