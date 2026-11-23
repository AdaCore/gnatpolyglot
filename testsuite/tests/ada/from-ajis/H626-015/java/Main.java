import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        System.out.println(!TestPackage.createNullRec().isPresent());
        System.out.println(!TestPackage.createNullAnArray().isPresent());
        System.out.println(TestPackage.isNullRec(true, null));
        System.out.println(TestPackage.isNullAnArray(true, null));
    }
}
