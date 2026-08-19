import com.adacore.libex5.exceptions.ExceptionsPackage;
import com.adacore.libex5.exceptions.Exc1;
import com.adacore.libex5.exceptions.Exc2;
import com.adacore.libex5.exceptions.Tag;
import com.adacore.gnatpolyglot.runtime.ada2java.ConstraintError;
import com.adacore.gnatpolyglot.runtime.ada2java.ProgramError;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

public class Main {

    static class Deriv extends Tag {
        @Override
        public void raiseExc() {
            throw new Exc2(new PolyglotString("Foo"));
        }
    }

    public static void main(String[] args) {
        Deriv d = new Deriv();

        try {
            ExceptionsPackage.raiseExc(false);
        } catch (Exc1 e) {
            System.out.println("Java caught Exc1: " + e.getMessage());
        }
        try {
            ExceptionsPackage.raiseExc(true);
        } catch (ProgramError e) {
            System.out.println("Java caught Program_Error: " + e.getMessage());
        }
        try {
            ExceptionsPackage.callRaiseExc(d);
        } catch (Exc2 e) {
            System.out.println("Java caught Exc1: " + e.getMessage());
        }
        try {
            ExceptionsPackage.raiseConstraint();
        } catch (ConstraintError e) {
            System.out.println("Java caught Exc1: " + e.getMessage());
        }
    }

}
