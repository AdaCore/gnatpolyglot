import com.adacore.libtest.test.Extract;
import com.adacore.libtest.test.ExtractLine;

public class Main {
    public static void main(String[] args) {
        Extract e = new Extract(0);

        e.setB(99);

        ExtractLine l = new ExtractLine(0);

        l.setA(42);

        l.printVals(e);
    }
}
