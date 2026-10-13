import java.util.Optional;

import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.pkg.PkgPackage;
import com.adacore.libtest.pkg.Rec;

public class Main {
    static Rec r = new Rec(42);

    public static void main(String[] args) {
        // A Java callback returning an access value, called by Ada. Ada may
        // keep the returned access: give the object to the library.
        r._setOwner(Owner.LIBRARY);
        PkgPackage.call(() -> Optional.of(r));
        PkgPackage.call(() -> Optional.empty());

        // An Ada callback returning an access value, called by Java
        System.out.println(PkgPackage.get().get().get().getI());
    }
}
