import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {
        System.out.println("LENGTH = " + TestPackage.getV().size());

        IntegerArray a = null;

        a = TestPackage.getV().clone();

        System.out.println("LENGTH = " + a.size());
        System.out.println("FIRST = " + a.getBegin());
        System.out.println("LAST = " + a.getEnd());

        a = new IntegerArray(1, 0);

        System.out.println("LENGTH = " + a.size());
        System.out.println("FIRST = " + a.getBegin());
        System.out.println("LAST = " + a.getEnd());
    }
}
