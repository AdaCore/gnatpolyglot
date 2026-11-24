import com.adacore.libtest.test.R;
import com.adacore.libtest.test.T;
import com.adacore.libtest.test.E;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] args) {
        TestPackage.makeCalls(new R());
        TestPackage.makeCalls(new R() {
            @Override
            public void initialize(T v, E v2) {
                v.setV(789);
            }
        });
    }
}
