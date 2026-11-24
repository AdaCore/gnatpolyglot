import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Root;

public class Main {

    public static void main(String[] argv) throws InterruptedException {
        // Create array of 2 Root elements and set their values
        Root.Array r = new Root.Array(1, 2);
        r.getUnslided(1).setV(41);
        r.getUnslided(2).setV(42);

        TestPackage.printAll(r);

        // F() returns array of (43, 44)
        TestPackage.printAll(TestPackage.f());

        r = null;

        // Wait for GC to finalize Ada objects
        long start = System.currentTimeMillis();
        boolean finished = false;
        while (!finished) {
            System.gc();
            System.runFinalization();
            long end = System.currentTimeMillis();
            if (TestPackage.getFinalized() >= 6) {
                finished = true;
            } else if ((end - start) > 60 * 1000) {
                finished = true;
            } else {
                Thread.sleep(100);
            }
        }
    }

}
