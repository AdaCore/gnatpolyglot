import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.Rec;
import com.adacore.libtest.test.RecTagged;

public class Main {
    public static void main(String[] args) {
        Rec r = new Rec(0);
        r.setV(52);
        r.printFAcc((Rec R) -> new PolyglotString(Integer.toString(R.getV())));

        RecTagged t = new RecTagged();

        System.out.println(t.f(r));
    }
}
