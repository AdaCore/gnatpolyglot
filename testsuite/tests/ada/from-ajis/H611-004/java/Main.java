import com.adacore.libtest.p.PPackage;
import com.adacore.libtest.p.AnException;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

public class Main {

    public static void main(String[] args) {
        try {
            PPackage.raiseAnException();
        } catch (AnException e) {
            System.out.println(
                "An_Exception caught from Java (1): [" + e.getMessage() + "]");
        }

        try {
            PPackage.callTheCallback(() -> {
                throw new AnException(
                    new PolyglotString("This is a message from Java"));
            });
        } catch (Exception e) {
            System.out.println(
                "exception caught from Java (2): [" + e.getClass() + ", " + e.getMessage() + "]");
        }

        try {
            PPackage.raiseConstraintError();
        } catch (Exception e) {
            System.out.println(
                "An_Exception caught from Java (3): [" + e.getClass() + ", " + e.getMessage() + "]");
        }
    }

}
