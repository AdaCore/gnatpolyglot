import java.util.Optional;

import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.Rec;
import com.adacore.libtest.test.TestPackage;

public class Main {
    static Optional<Rec> recFBody() {
        Rec result = new Rec(0, 0);
        result.setA(999);
        result.setB(888);

        // The access value is handed over to the Ada library.
        result._setOwner(Owner.LIBRARY);
        return Optional.of(result);
    }

    public static void main(String[] args) {
        TestPackage.callAndDisplay(Main::recFBody);
    }
}
