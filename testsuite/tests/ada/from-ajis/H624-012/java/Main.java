import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.AType;
import java.util.Vector;

public class Main {

    public static void main(String[] args) {
        // F_1 and F_2 are not yet bound:
        //   F_1 returns A_Type'Class (class-wide return not yet supported)
        //   F_2 returns A_Type_Acc (access to classwide type not yet supported)
        // Display_Finalize and Expected_Freed are global variables (not yet supported in Java).
        // The calls below show the desired behaviour once the features are implemented.

        Vector<AType> f1Objects = new Vector<AType>();

        for (int j = 0; j < 10; ++j) {
            f1Objects.add(TestPackage.f1());
        }

        for (int j = 0; j < 10; ++j) {
            TestPackage.f2();
        }

        System.out.println("BEFORE GC");
        TestPackage.setDisplayFinalize(true);
        f1Objects = null;
        long start = System.currentTimeMillis();
        boolean finished = false;
        while (!finished) {
            System.gc();
            System.runFinalization();
            if (TestPackage.getExpectedFreed() >= 10) {
                finished = true;
            }
            long end = System.currentTimeMillis();
            if ((end - start) > 60 * 1000) {
                finished = true;
            }
            if (!finished) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                }
            }
        }
        System.out.println("AFTER GC");

        TestPackage.setDisplayFinalize(false);
    }

}
