import com.adacore.libtest.Callbacks;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        Callbacks.Callback0 callback = (Integer a) -> a * 2;

        TestPackage.call(callback, 10);
    }
}
