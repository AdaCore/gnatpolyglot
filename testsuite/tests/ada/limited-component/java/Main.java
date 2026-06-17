import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Value;
import com.adacore.libtest.test.HasDefault;

public class Main {

    public static void main(String[] args) throws Throwable {
        Value val = TestPackage.getValue();
        System.out.println(val.getV().getI());
        HasDefault def = new HasDefault();
        System.out.println(def.getDef().getI());
    }
}
