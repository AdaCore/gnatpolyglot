import com.adacore.libtest.pkg.PkgPackage;
import com.adacore.libtest.pkg.Enum;

public class Main {
    public static void main(String[] args) {
        PkgPackage.callProc((Enum.Ref x) -> {
            System.out.println("Java: X = " + x.toString());
            x.setValue(Enum.C);
        });
    }
}

