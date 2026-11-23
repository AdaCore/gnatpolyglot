import com.adacore.libtest.test.Rec;
import com.adacore.libtest.test.TestPackage;

public class Main {

    // AJIS defines this class in a separate Java_Child.java file
    static class JavaChild extends Rec {
        @Override
        public void p1() {
            System.out.println("P_1 of Java_Child");
        }
    }

    public static void main(String[] argv) {
        Rec v1 = new Rec();
        Rec v2 = new JavaChild();

        v1.p1();
        v2.p1();

        TestPackage.callP1(true, v1);
        TestPackage.callP1(true, v2);
    }
}
