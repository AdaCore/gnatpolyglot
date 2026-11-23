import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {
        PolyglotString s1 = new PolyglotString("toto");
        // PolyglotString has no (first, last) constructor: allocate a
        // 10-character string instead.
        PolyglotString s2 = new PolyglotString("          ");

        TestPackage.print(s1);
        System.out.println(s1.toString());

        TestPackage.modify(s1);
        TestPackage.modify(s2);

        TestPackage.print(s1);
        System.out.println(s1.toString());

        TestPackage.print(s2);
        System.out.println(s2.toString());
    }
}
