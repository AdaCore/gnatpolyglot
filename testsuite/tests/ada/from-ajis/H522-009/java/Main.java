import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        PolyglotString v = new PolyglotString("YO");
        // F takes an access value: the library needs ownership.
        v._setOwner(Owner.LIBRARY);
        System.out.println(TestPackage.f(v).get());
    }
}
