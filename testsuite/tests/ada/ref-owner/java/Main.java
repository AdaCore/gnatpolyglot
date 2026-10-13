import com.adacore.gnatpolyglot.runtime.ada2java.*;
import com.adacore.libtest.pkg.PkgPackage;
import com.adacore.libtest.pkg.Rec;

public class Main {
    public static void main(String[] args) {
        // Each time, Ada stores a value it allocated in the ref: it is owned
        // by the library, so the ref can be passed back to Ada.

        // String
        PolyglotString.Ref str = new PolyglotString.Ref(null);
        PkgPackage.replace(str);
        PkgPackage.replace(str);
        System.out.println(str.get().get());

        // Arrays of native types
        IntegerArray.Ref i = new IntegerArray.Ref(null);
        PkgPackage.replace(i);
        PkgPackage.replace(i);
        System.out.println(i.get().get().getUnslided(1));

        ByteArray.Ref b = new ByteArray.Ref(null);
        PkgPackage.replace(b);
        PkgPackage.replace(b);
        System.out.println(b.get().get().getUnslided(1));

        ShortArray.Ref s = new ShortArray.Ref(null);
        PkgPackage.replace(s);
        PkgPackage.replace(s);
        System.out.println(s.get().get().getUnslided(1));

        LongArray.Ref l = new LongArray.Ref(null);
        PkgPackage.replace(l);
        PkgPackage.replace(l);
        System.out.println(l.get().get().getUnslided(1));

        FloatArray.Ref f = new FloatArray.Ref(null);
        PkgPackage.replace(f);
        PkgPackage.replace(f);
        System.out.println(f.get().get().getUnslided(1));

        DoubleArray.Ref d = new DoubleArray.Ref(null);
        PkgPackage.replace(d);
        PkgPackage.replace(d);
        System.out.println(d.get().get().getUnslided(1));

        BooleanArray.Ref o = new BooleanArray.Ref(null);
        PkgPackage.replace(o);
        PkgPackage.replace(o);
        System.out.println(o.get().get().getUnslided(1));

        // Array of a record type
        Rec.Array.Ref r = new Rec.Array.Ref(null);
        PkgPackage.replace(r);
        PkgPackage.replace(r);
        System.out.println(r.get().get().getUnslided(1).getV());
    }
}
