import com.adacore.libtest.ref.RefPackage;
import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;

public class Main {
    public static void main(String[] args) {
        IntegerArray b1 = new IntegerArray(1, 2);
        IntegerArray b2 = new IntegerArray(10, 11);

        b1.setUnslided(1, 1000);
        b1.setUnslided(2, 2000);
        b2.setUnslided(10, 3000);
        b2.setUnslided(11, 4000);

        IntegerArray b3 = RefPackage.f(b1, b2);

        for (int i = b3.getBegin(); i <= b3.getEnd(); i++) {
            System.out.println("[" + i + "] = " + b3.getUnslided(i));
        }
    }
}
