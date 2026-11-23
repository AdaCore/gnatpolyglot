import com.adacore.libtest.test.R;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        TestPackage.getV().setF1(5);
        TestPackage.getV().setF2(10);

        System.out.println
            ("F1, F2 = "
                + TestPackage.getV().getF1() + ", " + TestPackage.getV().getF2());

        R newObject = new R(0, 0);
        newObject.setF1(15);
        newObject.setF2(20);

        TestPackage.setV(newObject);

        System.out.println
            ("F1, F2 = "
                + TestPackage.getV().getF1() + ", " + TestPackage.getV().getF2());
    }
}
