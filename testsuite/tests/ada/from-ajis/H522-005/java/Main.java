import com.adacore.libtest.test.Child1;
import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        Root v1 = TestPackage.getObject(1).get();
        Root v2 = TestPackage.getObject(2).get();
        Root v3 = TestPackage.getObject(3).get();
        Root v4 = TestPackage.getObject(4).get();
        Root v5 = TestPackage.getObject(5).get();

        System.out.println("ROOT: " + Root.class);
        System.out.println("CHILD 1: " + Child1.class);

        System.out.println(v1.getClass());
        System.out.println(v2.getClass());
        System.out.println(v3.getClass());
        System.out.println(v4.getClass());
        System.out.println(v5.getClass());
    }
}
