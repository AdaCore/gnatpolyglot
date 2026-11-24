import com.adacore.libtest.test.T2;
import com.adacore.libtest.test.TestPackage;

public class Main {
    static class MyChild extends T2 {
        @Override
        public void p() {
            System.out.println("P_1 of My_Child");
        }

        @Override
        public void p2() {
            System.out.println("P_2 of My_Child");
        }
    }

    public static void main(String[] args) {
        TestPackage.callP(new MyChild());
        TestPackage.callP2(new MyChild());
    }
}
