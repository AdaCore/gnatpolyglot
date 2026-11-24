import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.Root;

public class Main {

    public static void main(String[] argv) {
        Root r = new Root();

        // PolyglotString has no (first, last) constructor: allocate a
        // 10-character string instead.
        PolyglotString s = new PolyglotString("          ");

        r.fill(s);
        System.out.println(s.toString());

        r.setFilling('z');
        r.fill(s);
        System.out.println(s.toString());
    }
}
