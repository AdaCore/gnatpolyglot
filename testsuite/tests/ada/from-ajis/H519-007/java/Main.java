import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.MyType;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        // Match C++: cases 1 and 3. Case 2 uses unsupported anonymous access.
        try (MyType obj = new MyType()) {
            obj.setFields(10, 199.9f);
            obj.displayMyType();

            obj._setOwner(Owner.LIBRARY);
            try {
                TestPackage.setFields3(obj, 30, 399.9f);
                TestPackage.displayMyType3(obj);
            } finally {
                // Neither procedure retains the object. Restore Java ownership.
                obj._setOwner(Owner.USER);
            }
        }
    }
}
