import com.adacore.libtest.Callbacks;
import com.adacore.libtest.test.ARecord;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        Callbacks.Callback0 callback = (Integer v, ARecord w) -> {
            w.setV(w.getV() + 1);

            return w;
        };

        TestPackage.call(callback);
    }
}
