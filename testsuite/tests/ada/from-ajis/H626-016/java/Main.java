import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        PolyglotString.Ref r = new PolyglotString.Ref(null);

        TestPackage.createString(r);

        PolyglotString s1 = r.get().get();

        System.out.println(s1.toString());

        TestPackage.createAndAppendString(r);

        PolyglotString s2 = r.get().get();

        System.out.println(s1.toString());
        System.out.println(s2.toString());
    }
}
