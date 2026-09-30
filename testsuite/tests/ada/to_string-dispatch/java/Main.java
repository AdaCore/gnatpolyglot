import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.pkg.PkgPackage;
import com.adacore.libtest.pkg.T;

public class Main {
    public static class MyT extends T {
        @Override
        public PolyglotString toString_() {
            return new PolyglotString("My T");
        }
    }

    public static void main(String[] args) {
        PkgPackage.print(new T());
        PkgPackage.print(new MyT());
    }
}
