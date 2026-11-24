import com.adacore.libtest.test.ARecord;
import com.adacore.libtest.test.ARecordTagged;
import com.adacore.libtest.test.AType;
import com.adacore.libtest.test.TestPackage;

public class Main {

    static class RecChild extends ARecordTagged {

    }

    public static void main(String[] args) {
        ARecord rec = new ARecord();
        ARecordTagged rec2 = TestPackage.getARecordTagged().get();
        AType typ = new AType();

        TestPackage.callAProc(typ, rec, rec2);

        //  We used not to do properly the copy on the in out mode, and lose
        //  the value modification in the process, displaying 999 instead of
        //  1024.
        System.out.println(rec.getAValue() + ", " + rec2.getAValue());

        //  Check that it still works with extended types.

        ARecordTagged rec3 = new ARecordTagged();
        ARecordTagged rec4 = new RecChild();

        TestPackage.callAProc(typ, rec, rec3);
        TestPackage.callAProc(typ, rec, rec4);

        System.out.println(rec3.getAValue());
        System.out.println(rec4.getAValue());
    }
}
