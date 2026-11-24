import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] args) {
        System.out.print(TestPackage.create().get().f().toString());
    }
}
