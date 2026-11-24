import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.PAcc;
import com.adacore.libtest.test.PAcc2;
import com.adacore.libtest.test.PAcc3;
import com.adacore.libtest.test.PAcc4;
import com.adacore.libtest.test.Rec;
import com.adacore.libtest.test.Rec2;
import com.adacore.libtest.test.Rec3;
import com.adacore.libtest.test.Rec4;

public class Main {
    public static void main(String[] args) {
        TestPackage.strange(new PAcc() {
            public void body(Rec[] v) {
                System.out.println("[JAVA - 1] " + v[0]);
                v[0] = null;
            }
        });
        TestPackage.strange2(new PAcc2() {
            public void body(Rec2[] v) {
                System.out.println("[JAVA - 2] " + v[0]);
                v[0] = null;
            }
        });
        TestPackage.strange3(new PAcc3() {
            public void body(Rec3[] v) {
                System.out.println("[JAVA - 3] " + v[0]);
                v[0] = new Rec3();
            }
        });
        TestPackage.strange4(new PAcc4() {
            public void body(Rec4[] v) {
                System.out.println("[JAVA - 4] " + v[0]);
                v[0] = new Rec4();
            }
        });
        TestPackage.strange3(new PAcc3() {
            public void body(Rec3[] v) {
                System.out.println("[JAVA - 3] " + v[0]);
                v[0] = new Rec3();
            }
        });
        TestPackage.strange4(new PAcc4() {
            public void body(Rec4[] v) {
                System.out.println("[JAVA - 4] " + v[0]);
                v[0] = new Rec4();
            }
        });
    }
}
