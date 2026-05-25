import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Tag;
import com.adacore.libtest.test.Exc1;
import com.adacore.libtest.test.Exc2;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.gnatpolyglot.runtime.ada2java.AdaException;
import com.adacore.gnatpolyglot.runtime.ada2java.ProgramError;

public class Main {

    public static class Deriv extends Tag{
        public Deriv(int i) {
            super(i);
        }

        @Override
        public void raiseExc() {
            switch (getI()) {
                case 1:
                    throw new Exc1(new PolyglotString("Foo"));
                case 2:
                    throw new Exc2(new PolyglotString("Bar"));
                case 3:
                    throw new AdaException(new PolyglotString("Baz"));
                case 4:
                    throw new ProgramError(new PolyglotString("FooBar"));
                case 5:
                    throw new RuntimeException("host language");
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        Deriv d = new Deriv(1);
        try {
            TestPackage.getException(d);
        } catch (Exc1 e) {
            System.out.println("Java caught Exc1: " + e.getMessage());
        }
        try {
            d.setI(2);
            TestPackage.getException(d);
        } catch (Exc2 e) {
            System.out.println("Java caught Exc2: " + e.getMessage());
        }
        try {
            d.setI(3);
            TestPackage.getException(d);
        } catch (AdaException e) {
            System.out.println("Java caught AdaException: " + e.getMessage());
        }
        try {
            d.setI(4);
            TestPackage.getException(d);
        } catch (ProgramError e) {
            System.out.println("Java caught Program_Error: " + e.getMessage());
        }
        try {
            d.setI(5);
            TestPackage.getException(d);
        } catch (AdaException e) {
            System.out.println("Java caught AdaException: " + e.getMessage());
        }
    }
}
