import com.adacore.libtest.test.Pair;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.derivation.OtherPair;
import com.adacore.libtest.derivation.DerivationPackage;

public class Main {
    static Pair t(Pair p) {
        p.print();
        Pair p2 = new Pair();
        p2.print();
        p.addPairs(TestPackage.initPair(4, 3)).print();

        return p;
    }

    public static void main(String[] args) {
        Pair p = TestPackage.initPair(3, 4);
        System.out.format("%d\n", p.left());
        System.out.format("%d\n", p.sum());

        t(p);
        p.print();

        OtherPair op = DerivationPackage.initPair(1, 2);
        op.print();
    }
}
