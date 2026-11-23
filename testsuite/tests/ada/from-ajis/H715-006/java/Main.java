import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] args) {
        TestPackage.getVAll().setY(10);
        TestPackage.getVAll().setZ(999);

        System.out.println(TestPackage.getV().get().getY());
        System.out.println(TestPackage.getV().get().getZ());
    }
}
