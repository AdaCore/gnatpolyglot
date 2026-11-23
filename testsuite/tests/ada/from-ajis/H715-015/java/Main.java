import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        System.out.println(TestPackage.getC1().getUnslided(1));

        TestPackage.getC1().setUnslided(1, 2001);

        System.out.println(TestPackage.getC1().getUnslided(1));

        System.out.println(TestPackage.getC2().getUnslided(1));

        TestPackage.getC2().setUnslided(1, 2001);

        System.out.println(TestPackage.getC2().getUnslided(1));
    }
}
