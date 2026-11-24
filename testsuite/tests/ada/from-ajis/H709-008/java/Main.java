import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.R;

public class Main {
    public static void main(String[] args) {
        try (R r = new R(999)) {
            r._setOwner(Owner.LIBRARY);
            try {
                TestPackage.procWithOwnedAllowed(r);
                TestPackage.procWithOwnedNotAllowed(r);
            } finally {
                // Neither procedure retains the object. Restore Java ownership.
                r._setOwner(Owner.USER);
            }
        }
    }
}
