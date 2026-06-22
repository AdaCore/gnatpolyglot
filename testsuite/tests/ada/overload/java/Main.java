import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.T1;

public class Main {

    public static void main(String[] args) throws Throwable {
        T1 t = new T1(0);

        TestPackage.overloadedProc();
        TestPackage.overloadedProc(1);
        System.out.println("Got from ada:" + TestPackage.overloadedFun(1, 2, 3));
        System.out.println("Got from ada:" + TestPackage.overloadedFun(1, t, t));

        TestPackage.voidOverloadedRet();
        System.out.println("Got from ada:" + TestPackage.standardIntegerOverloadedRet());
        System.out.println("Got from ada:" + TestPackage.testT1OverloadedRet().getV());
        System.out.println("Got from ada:" + TestPackage.standardIntegerOverloadedRet(4));

        System.out.println("10 * 2 = " + TestPackage.noRename(10));
        System.out.println("10L * 4 = " + TestPackage.noRename((short) 10, (short) 4));

        TestPackage.overloadedInt(1);
        TestPackage.overloadedInt1(2);
    }
}
