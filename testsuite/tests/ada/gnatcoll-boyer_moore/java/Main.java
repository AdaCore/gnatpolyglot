import com.adacore.libgnatcoll_minimal.gnatcoll.boyer_moore.Pattern;
import com.adacore.libgnatcoll_minimal.Library;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;


public class Main {

    public static void main(String[] args) throws Throwable {
        PolyglotString str = new PolyglotString("ABDEABCFGABC");
        Pattern key = new Pattern();
        key.compile(new PolyglotString("ABC"), true);
        System.out.println(key.search(str));
        key.free();
    }
}
