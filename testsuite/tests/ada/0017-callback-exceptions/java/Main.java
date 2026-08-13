import com.adacore.libtest.test.TestPackage;

import com.adacore.gnatpolyglot.runtime.ada2java.ProgramError;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

public class Main {

    static void foo() {
        throw new ProgramError(new PolyglotString("bar"));
    }

    public static void main(String[] args) throws Throwable {
        try {
            TestPackage.getCallback().run();
        } catch (ProgramError e) {
            System.out.println("Java caught : " + e.getMessage());
        }

        try {
            TestPackage.call(Main::foo);
        } catch (ProgramError e) {
            System.out.println("Java caught : " + e.getMessage());
        }

    }
}
