import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;
import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {
        IntegerArray.Ref ref = new IntegerArray.Ref(null);

        ref.set(null);

        TestPackage.setToNew(ref);

        System.out.println(ref.get().get().getUnslided(1));

        TestPackage.setToNew(ref);

        System.out.println(ref.get().get().getUnslided(1));

        TestPackage.setToNull(ref);

        System.out.println(ref.get().orElse(null));

        TestPackage.setToNull(ref);

        System.out.println(ref.get().orElse(null));

        IntegerArray x = new IntegerArray(1, 2);

        x.setUnslided(1, 998);
        x.setUnslided(2, 999);

        ref.set(x);

        x._setOwner(Owner.LIBRARY);

        TestPackage.callSetToNullAndPrint(new Root() {

            @Override
            public void setToNull(IntegerArray.Ref v) {
                v.set(null);
            }
        }, ref);

        System.out.println(ref.get().orElse(null));
    }
}
