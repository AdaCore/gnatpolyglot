import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Rec;

public class Main {

    public static void main(String[] args) throws Throwable {
        Rec acc = new Rec(1);
        acc._setOwner(Owner.LIBRARY);
        TestPackage.setGlobalAcc(acc);
        System.out.println(acc.getI());
        TestPackage.foo();
        TestPackage.foo();
        System.out.println(acc.getI());
        TestPackage.foo();
        System.out.println(TestPackage.getGlobalAcc().get().getI());
        TestPackage.bar();
        System.out.println(TestPackage.getGlobalAcc().orElse(null));
        acc._setOwner(Owner.USER);
    }
}
