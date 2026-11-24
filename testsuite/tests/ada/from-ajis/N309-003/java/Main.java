import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.T1;
import com.adacore.libtest.test.T2;

public class Main {

    public static void main(String[] args) {
        T1 t1 = new T1();
        // The discriminant of t2 keeps an access to t1
        t1._setOwner(Owner.LIBRARY);
        T2 t2 = new T2(t1);
        if (t2.getI() == 1961) {
            System.out.println("OK");
        }
    }
}
