import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.NotInt;
import com.adacore.libtest.test.child.ChildPackage;
import com.adacore.libtest.test.child.Rec;

public class Main {

    public static void main(String[] args) throws Throwable {
        NotInt i = TestPackage.get();
        ChildPackage.f(i);
        Rec r = new Rec(i);
        i = r.getI();
        r.setI(ChildPackage.getG());
    }
}
