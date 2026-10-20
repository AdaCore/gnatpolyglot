import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.Child1;
import com.adacore.libtest.test.Child2;
import com.adacore.libtest.test.Ddtr;
import com.adacore.libtest.test.Priv;
import com.adacore.libtest.test.R;
import com.adacore.libtest.test.Wrapper;

import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

public class Main {

    static class DDTRChild extends Ddtr {
        public DDTRChild() {}
        public DDTRChild(int a) {
            super(a);
        }
    }

    public static void main(String[] args) {
        Root r = new Root(1, 2);
        System.out.println(r.getA() + " " + r.getB());

        Child1 c1 = new Child1(2);
        System.out.println(c1.getA() + " " + c1.getB() + " " + c1.getC());

        Child2 c2 = new Child2(2, 3);
        System.out.println(c2.getA() + " " + c2.getB());

        R rec = new R(3, new PolyglotString("abc"));
        System.out.println(rec.getS().toString());
        try {
            new R(2, new PolyglotString("abc"));
        } catch (Throwable t) {
            System.out.println("caught: " + t.getMessage());
        }

        Wrapper nullRef = new Wrapper();
        System.out.println(nullRef.getAcc().orElse(null));

        Wrapper ref = new Wrapper(rec);
        System.out.println(ref.getAcc().get().getS().toString());

        Priv priv1 = new Priv();
        Priv priv2 = new Priv(1);
        System.out.println(priv2.getI() + " " + priv2.getJComponent());

        Ddtr ddtr1 = new Ddtr();
        Ddtr ddtr2 = new Ddtr(4);
        System.out.println(new DDTRChild().getA());
    }
}
