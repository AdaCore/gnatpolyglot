import com.adacore.libtest.Callbacks;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {
        TestPackage.callT(new Callbacks.Callback0() {

            @Override
            public void accept(Integer v) {
                System.out.println("GOT V = " + v);
            }
        });
    }
}
