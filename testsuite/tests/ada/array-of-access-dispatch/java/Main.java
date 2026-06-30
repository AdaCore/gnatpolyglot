import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Rec.PtrArray.Ref;
import com.adacore.libtest.test.Rec;
import com.adacore.libtest.test.T;

import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class Main {

    static class Child extends T {
        @Override
        public void foo(Rec.PtrArray.Ref a) {
            Rec.PtrArray res = TestPackage.incArr(a.get().get());
            res._setOwner(Owner.LIBRARY);
            a.set(res);
        }
    }

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

        Child c = new Child();
        Rec.PtrArray.Ref ref = new Rec.PtrArray.Ref(arr);
        arr._setOwner(Owner.LIBRARY);
        TestPackage.callFoo(c, ref);
        Rec.PtrArray arr2 = ref.get().get();
        assert !arr._getData().equals(arr2._getData());
        assert arr.get(0)._getData().equals(arr2.get(0)._getData());
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
