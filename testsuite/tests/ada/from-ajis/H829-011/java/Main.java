import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        System.out.println(TestPackage.getLr().getV());

        TestPackage.getLr().setV(2002);
        TestPackage.getLr().getV2().setX(666);

        System.out.println(TestPackage.getLr().getV());
        System.out.println(TestPackage.getLr().getV2().getX());
    }
}
