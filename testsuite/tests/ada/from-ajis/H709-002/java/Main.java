import com.adacore.libtest.test.ARecord;
import com.adacore.libtest.test.TestPackage;

public class Main {

    static class RecChild extends ARecord {
        int f = 999;

        @Override
        public void proc() {
            super.proc();
            f = 1024;
        }
    }

    public static void main(String[] args) {
        ARecord r1, r2, r3;

        r1 = TestPackage.getObj().get();
        r2 = new ARecord();
        r3 = new RecChild();

        TestPackage.callProc(r1);
        TestPackage.callProc(r2);
        TestPackage.callProc(r3);

        System.out.println(r1.getAValue());
        System.out.println(r2.getAValue());
        System.out.println(r3.getAValue());
        System.out.println(((RecChild) r3).f);
    }
}
