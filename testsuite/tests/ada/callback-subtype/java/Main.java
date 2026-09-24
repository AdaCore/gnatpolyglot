import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] args) throws Throwable {
        System.out.println(TestPackage.call(i -> i + 1));
        System.out.println(TestPackage.getCallback().apply(4));
    }
}
