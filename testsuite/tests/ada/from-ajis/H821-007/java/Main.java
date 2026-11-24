import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.ATaggedType;
import com.adacore.libtest.test.ANonTaggedType;
import com.adacore.libtest.test.ATaggedCallback;
import com.adacore.libtest.test.ANonTaggedCallback;

public class Main {
    public static void main(String[] args) {
        // Get a non-tagged object (bound)
        ANonTaggedType obj = TestPackage.getNonTaggedObject();
        System.out.println("got non-tagged object");

        // Set and get tagged object (Set_Tagged_Object not bound: uses A_Tagged_Access)
        // Get_Tagged_Object not bound: returns class-wide
        ATaggedType t = new ATaggedType();
        TestPackage.setTaggedObject(t);
        ATaggedType got = TestPackage.getTaggedObject();

        // Call callbacks (not bound: access-to-subprogram params)
        ATaggedCallback cb1 = (ATaggedType v) -> {
            System.out.println("tagged callback called");
        };
        TestPackage.callTaggedCallback(cb1, t);

        ANonTaggedCallback cb2 = (ANonTaggedType v) -> {
            System.out.println("non-tagged callback called");
        };
        ANonTaggedType y2 = new ANonTaggedType();
        TestPackage.callNonTaggedTypeCallback(cb2, y2);
    }
}
