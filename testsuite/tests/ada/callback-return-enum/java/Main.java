import com.adacore.libtest.pkg.PkgPackage;
import com.adacore.libtest.pkg.Enum;

public class Main {
    public static void main(String[] args) throws Throwable {
        PkgPackage.callFunc((n) -> n > 0 ? Enum.C : Enum.A);
    }
}
