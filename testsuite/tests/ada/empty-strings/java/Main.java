import com.adacore.libtest.test.TestPackage;

import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

public class Main {

    public static void main(String[] args) throws Throwable {
        PolyglotString s = TestPackage.printAndReturn(new PolyglotString());
        System.out.println(s.toString());
    }
}
