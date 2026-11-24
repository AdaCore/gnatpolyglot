import com.adacore.libtest.test.Child1;
import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.TestPackage;

public class Main {

    static class JavaChild extends Root {
        @Override
        public void p1() {
            System.out.println("P_1 of Java_Child");
        }
    }

    public static void main(String[] args) {
        Root v1 = new Child1();
        Root v2 = TestPackage.getDirectRootChild().get();
        Root v3 = new JavaChild();
        v1.p1();
        v2.p1();
        v3.p1();
        TestPackage.displayP1(true, v1);
        TestPackage.displayP1(true, v2);
        TestPackage.displayP1(true, v3);
    }
}
