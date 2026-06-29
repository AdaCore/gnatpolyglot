import com.adacore.libtest.tagged_type.T;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.A;

import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class Main {

    public static void main(String[] args) throws Throwable {
        A v = new A(1);
        A x = v.identity();
        v.setV(2);
        System.out.println(v.getV() + " " + x.getV());

        TestPackage.foo(new T());
        v._setOwner(Owner.LIBRARY);
        System.out.println(TestPackage.identity(v).get().getV());
        v._setOwner(Owner.USER);
    }

}
