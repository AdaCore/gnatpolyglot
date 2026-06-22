import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Value;

public class Main {

    public static void main(String[] args) throws Throwable {
        try {
            Value.class.getConstructor();
            System.out.println("found ctor");
        }
        catch (NoSuchMethodException e) {}
        Value v = TestPackage.initValue(3, 4);
        v.p();
        v.p();
    }
}
