import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.Callbacks;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        Callbacks.Callback0 myProc = (PolyglotString text) -> {
            System.out.println(text);
        };

        TestPackage.doSomething(myProc);
    }
}
