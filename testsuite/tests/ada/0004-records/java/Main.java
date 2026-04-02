import com.adacore.libtest.test.Pair;
import com.adacore.libtest.test.PairBis;
import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Value;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.adacore.libtest.test.HalfDefault;

public class Main {

    public static void main(String[] args) {
        Pair p = TestPackage.initPair(3, 4);
        p.print();

        Value v = new Value(p.getV1());
        System.out.format("p.v_1.v = %d\n", v.getV());

        v.setV(-1);

        // the value of "p.v_1.v" should stay the same: the record has been copied.
        System.out.format("v.v = %d\n", v.getV());
        System.out.format("p.v_1.v = %d\n", p.getV1().getV());

        p.getV1().setV(5);
        p.getV2().setV(6);
        p.print();

        Value valView1 = p.getV1();
        valView1.setV(7);
        Value valView2 = p.getV2();
        valView2.setV(8);
        p.print();

        // Test the Ctor
        Pair ctor = new Pair(new Value(4), new Value(2));
        ctor.print();

        PairBis bis = new PairBis(new Value(0), new Value(0));
        bis.getV1().setV(2);
        bis.getV2().setV(4);
        bis.printBis();
        bis.print();

        HalfDefault h1 = new HalfDefault(new Value(2), 3);
        HalfDefault h2 = new HalfDefault(3);
    }
}
