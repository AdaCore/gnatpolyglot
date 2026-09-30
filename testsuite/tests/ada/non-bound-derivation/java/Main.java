import com.adacore.libtest.test.Child;
import com.adacore.libtest.test.GChild;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Root;

public class Main {
    public static void main(String[] args) {
        Root v1 = TestPackage.getObject1().get();
        System.out.println(v1 instanceof Child);
        System.out.println(v1 instanceof GChild);
        Root v2 = TestPackage.getObject2().get();
        System.out.println(v2 instanceof GChild);
    }
}
