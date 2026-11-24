import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.hello.HelloPackage;

public class Main {
    public static void main(String[] args) {
        HelloPackage.say(new PolyglotString("OK"));
    }
}
