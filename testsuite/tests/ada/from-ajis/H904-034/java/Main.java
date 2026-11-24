import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        TestPackage.call(() -> {
            throw new RuntimeException("callback failed");
        });
    }
}
