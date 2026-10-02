import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.R;
import com.adacore.libtest.test.T;

public class Main {

    static R obj = new R(2007);

    static class Child extends T {
        @Override
        public int p(R.Ref v) {
            System.out.println("X is " + v.get().get().getV());
            v.get().get()._setOwner(Owner.USER);
            v.set(obj);
            return 0;
        }
    }

    public static void main(String[] args) throws Throwable {
        TestPackage.callP(x -> {
            System.out.println("X is " + x.get().get().getV());
            x.get().get()._setOwner(Owner.USER);
            x.set(obj);
            return 0;
        });

        System.exit(
            TestPackage.callP(new Child())
        );
    }
}
