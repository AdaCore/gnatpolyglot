import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Rec;

public class Main {

    public static void main(String[] args) throws Throwable {
        var arr = TestPackage.foo();
        arr.get(1).run();
        for (var it : arr) {
            it.run();
        }
        try {
            TestPackage.foo().set(1, null).run();
        } catch (Throwable t) {
            System.out.println("caught exc: " + t.getMessage());
        }
        new Rec().getC().run();
    }
}
