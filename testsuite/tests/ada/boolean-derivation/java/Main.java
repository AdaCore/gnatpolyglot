import com.adacore.libtest.test.MyBool;
import com.adacore.libtest.test.TestPackage;
import com.adacore.gnatpolyglot.runtime.BooleanRef;

public class Main {

    public static void main(String[] args) throws Throwable {
        TestPackage.printMyBool(MyBool.TRUE);
        TestPackage.printMyBool(MyBool.FALSE);
    }
}
