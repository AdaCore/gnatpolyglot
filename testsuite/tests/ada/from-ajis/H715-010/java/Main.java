import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] args) {
        TestPackage.setA(1);
        TestPackage.setB(2);
        TestPackage.setC(3);

        System.out.println(
            TestPackage.getA() + ", " + TestPackage.getB() + ", " + TestPackage.getC());
    }
}
