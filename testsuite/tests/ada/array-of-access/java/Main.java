import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Rec;

import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class Main {

    public static void main(String[] args) throws Throwable {
        Rec r1 = new Rec(0);
        r1._setOwner(Owner.LIBRARY);
        Rec r2 = new Rec(1);
        r2._setOwner(Owner.LIBRARY);
        Rec r3 = new Rec(2);
        r3._setOwner(Owner.LIBRARY);
        Rec.PtrArray arr = new Rec.PtrArray(1, 3);
        arr.setUnslided(1, r1);
        arr.setUnslided(2, r2);
        arr.setUnslided(3, r3);
        for (int i = 1; i <= 3; i++) {
            System.out.print(arr.getUnslided(i).getI() + ", ");
        }
        System.out.println();
        Rec.PtrArray arr2 = TestPackage.incArr(arr);
        for (int i = 1; i <= 3; i++) {
            System.out.print(arr.getUnslided(i).getI() + ", ");
        }
        System.out.println();
        for (int i = 1; i <= 3; i++) {
            System.out.print(arr2.getUnslided(i).getI() + ", ");
        }
        System.out.println();

    }
}
