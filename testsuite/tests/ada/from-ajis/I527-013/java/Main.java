import com.adacore.libtest.p.PPackage;
import com.adacore.libtest.base.T;

public class Main {
    public static void main(String[] args) {
        PPackage.call((T x) -> {
            System.out.println(x.getV());
        });
    }
}
