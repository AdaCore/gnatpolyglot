import com.adacore.libtest.test.TestPackage;

import java.util.Optional;

import com.adacore.libtest.test.Holder;
import com.adacore.libtest.test.R;
import com.adacore.libtest.test.T;

public class Main {

    public static void main(String[] args) throws Throwable {
        Optional<T> t = TestPackage.id(null);

        // Out parameter
        T.Ref tRef = new T.Ref(null);
        TestPackage.get(tRef);
        System.out.println(tRef.get().isPresent());

        // Callback parameter
        TestPackage.call(a -> System.out.println(a != null));

        // Callback out parameter
        TestPackage.callOut(a -> TestPackage.get(a));

        // Non-tagged type: global variable getter
        R x = TestPackage.getX().get();
        System.out.println(x.getV());

        // Function result
        System.out.println(TestPackage.idR(x).get().getV());

        // Out parameter
        R.Ref r = new R.Ref(null);
        TestPackage.getR(r);
        System.out.println(r.get().get().getV());

        // Component getter
        Holder h = new Holder(x);
        System.out.println(h.getAcc().get().getV());

        // Callback parameter
        TestPackage.callR(a -> System.out.println(a.getV()));

        // Callback out parameter
        TestPackage.callOutR(a -> a.set(TestPackage.getX().get()));
    }
}
