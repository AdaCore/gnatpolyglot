import com.adacore.libtest.test.ARecord;
import com.adacore.libtest.test.AccessedByPointer;
import com.adacore.libtest.test.BaseRecord;

public class Main {

    public static void main(String[] argv) throws InterruptedException {
        ARecord.Array arr = new ARecord.Array(1, 1000);

        ARecord r;

        r = arr.getUnslided(5);
        r.getRec().setV(99);

        arr = null;

        System.gc();
        Thread.sleep(1000);

        System.out.println(r.getRec().getV());

        r.getRec().setV(100);

        System.out.println(r.getRec().getV());

        BaseRecord base = r.getRec();

        r = null;

        System.gc();
        Thread.sleep(1000);

        System.out.println(base.getV());

        base.setV(101);

        System.out.println(base.getV());

        base = null;

        System.gc();
        Thread.sleep(1000);

        r = new ARecord();

        base = r.getRec();

        base.setV(102);

        r = null;

        System.gc();
        Thread.sleep(1000);

        System.out.println(base.getV());

        base.setV(103);

        System.out.println(base.getV());

        base = null;

        System.gc();
        Thread.sleep(1000);

        base = new BaseRecord();

        base.setV(104);

        AccessedByPointer p = base.getW().get();

        base = null;

        System.gc();
        Thread.sleep(1000);

        p.setV(105);

        System.out.println(p.getV());
    }

}
