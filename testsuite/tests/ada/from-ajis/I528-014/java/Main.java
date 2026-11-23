import com.adacore.libtest.p.PPackage;
import com.adacore.libtest.p.R;
import com.adacore.libtest.p.T;
import com.adacore.libtest.p.TChild;

public class Main {
    public static void main(String[] args) {
        PPackage.a(0);
        // B (V : R) is bound as a primitive method of R
        new R().b();
        new T().c();
        new TChild().c();
        PPackage.d(0);
        // D (V : D_Type) is disambiguated as d1 (AJIS: Rename pragma D_Renamed)
        PPackage.d1(0);
    }
}
