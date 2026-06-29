import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Pair;

import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

public class Main {

    static void print(Pair p) {
        System.out.println("{%d, %d}".formatted(p.getLeft(), p.getRight()));
    }

    public static void main(String[] args) throws Throwable {
        print(new Pair(1, 2).operatorPlus(new Pair(3, 4)));
        System.out.println(new Pair(1, 3).operatorPlus());
        print(new Pair(3, 4).operatorMinus(new Pair(1, 2)));
        System.out.println(new Pair(1, 3).operatorMinus());
        print(new Pair(2, 3).operatorMult(new Pair(4, 5)));
        print(new Pair(20, 15).operatorDiv(new Pair(4, 5)));
        print(new Pair(2, 2).operatorPow(new Pair(4, 5)));
        print(new Pair(20, 15).operatorMod(new Pair(4, 5)));
        print(new Pair(20, 15).operatorRem(new Pair(4, 5)));
        print(new Pair(-15, 15).operatorAbs());
        print(new Pair(20, 15).operatorConcat(new Pair(4, 5)));
        System.out.println(new Pair(1, 2).operatorEq(new Pair(1, 2)));
        System.out.println(new Pair(1, 3).operatorEq(new Pair(1, 2)));
        System.out.println(new Pair(1, 2).operatorNe(new Pair(1, 2)));
        System.out.println(new Pair(1, 3).operatorNe(new Pair(1, 2)));
        print(new Pair(1, 2).operatorBitAnd(new Pair(3, 4)));
        print(new Pair(1, 2).operatorBitOr(new Pair(3, 4)));
        print(new Pair(-1, -1).operatorBitXor(new Pair(1, 2)));
        print(new Pair(~3, ~4).operatorBitNot());

        System.out.println(TestPackage.operatorMult(4, 'c').toString());
        System.out.println(TestPackage.operatorPlus(new PolyglotString("foo")).toString());
    }
}
