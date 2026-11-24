import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.AType;

public class Main {

    public static void main(String[] args) {
        AType t = new AType(new PolyglotString("YO"), 3, new PolyglotString("   "));
        t.setLocStr(new PolyglotString("123"));
        System.out.println(t.getLocStr());
    }
}
