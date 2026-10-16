import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.pkg.PkgPackage;
import com.adacore.libtest.pkg.Rec;
import com.adacore.libtest.pkg.T;

public class Main {
    // An override returning a record that remains in use after the call
    static class ChildField extends T {
        Rec field = new Rec(6);

        @Override
        public Rec echo(Rec r) {
            return field;
        }
    }

    public static void main(String[] args) {
        // A callback returning the record it received
        PkgPackage.call(r -> r);

        // An override returning the record it received
        PkgPackage.callEcho(new T() {
            @Override
            public Rec echo(Rec r) {
                return r;
            }
        });

        // A callback returning a record that remains in use after the call
        Rec outer = new Rec(5);
        PkgPackage.call(r -> outer);
        System.out.println("after callback: " + outer.getI());

        ChildField cField = new ChildField();
        PkgPackage.callEcho(cField);
        System.out.println("after override: " + cField.field.getI());

        // The same for arrays: callbacks returning the array they received,
        // then an array that remains in use after the call.
        PkgPackage.callIntArray(a -> a);
        IntegerArray outerArray = new IntegerArray(1, 1);
        outerArray.setUnslided(1, 7);
        PkgPackage.callIntArray(a -> outerArray);
        System.out.println("after callback: " + outerArray.getUnslided(1));

        PkgPackage.callRecArray(a -> a);
        Rec.Array outerRecArray = new Rec.Array(1, 1);
        outerRecArray.setUnslided(1, new Rec(8));
        PkgPackage.callRecArray(a -> outerRecArray);
        System.out.println("after callback: " + outerRecArray.getUnslided(1).getI());

        // The same for strings: a callback returning the string it received,
        // then a string that remains in use after the call.
        PkgPackage.callString(s -> s);
        PolyglotString outerString = new PolyglotString("B");
        PkgPackage.callString(s -> outerString);
        System.out.println("after callback: " + outerString);
    }
}
