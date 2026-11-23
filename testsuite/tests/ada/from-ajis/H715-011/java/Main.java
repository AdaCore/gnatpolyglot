import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] args) {
        TestPackage.say(new PolyglotString("Hello World!"));
    }
}
