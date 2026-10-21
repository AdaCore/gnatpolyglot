import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.pkg.PkgPackage;
import com.adacore.libtest.pkg.Rec;

public class Main {
    public static void main(String[] args) {
        // Each value is kept by Ada after the setter returns: give it to
        // the library. After each setter, Clobber reuses the stack area of
        // the generated setter, then Ada prints the bounds it stored.

        // Package-level String_Access variable
        PolyglotString s = new PolyglotString("Hello");
        s._setOwner(Owner.LIBRARY);
        PkgPackage.setV(s);
        PkgPackage.clobber();
        System.out.println("V bounds:" + PkgPackage.vBounds());

        // Package-level access-to-unconstrained-array variable
        IntegerArray a = new IntegerArray(10, 12);
        a._setOwner(Owner.LIBRARY);
        PkgPackage.setA(a);
        PkgPackage.clobber();
        System.out.println("A bounds:" + PkgPackage.aBounds());

        // Record component of type String_Access
        Rec r = new Rec((PolyglotString) null);
        PolyglotString c = new PolyglotString("World!");
        c._setOwner(Owner.LIBRARY);
        r.setC(c);
        PkgPackage.clobber();
        System.out.println("C bounds:" + r.cBounds());

        // Null value
        PkgPackage.setV(null);
        System.out.println("V is null: " + PkgPackage.vIsNull());
    }
}
