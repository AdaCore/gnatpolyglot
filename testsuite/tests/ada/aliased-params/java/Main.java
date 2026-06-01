import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Rec;
import com.adacore.gnatpolyglot.runtime.IntegerRef;
import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;

public class Main {

    public static void main(String[] args) throws Throwable {
        IntegerRef i = new IntegerRef(1);
        TestPackage.pInt(i.getValue());
        TestPackage.pIntOut(i);
        TestPackage.pInt(i.getValue());

        Rec rec = new Rec(1);
        rec.pRec();
        rec.pRecOut();
        rec.pRec();

        IntegerArray arr = new IntegerArray(1, 5);
        for (int index = 0; index < arr.size(); index++) {
            arr.set(index, 1);
        }
        TestPackage.pArr(arr);
        TestPackage.pArrOut(arr);
        TestPackage.pArr(arr);
    }
}
