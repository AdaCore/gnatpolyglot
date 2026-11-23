import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.AType;

public class Main {

    public static void main(String[] args) {
        // Create an A_Type with F = 99
        AType v = new AType(99);

        // Set_X is not bound: anonymous access A_Type'Class parameter not yet supported.
        // X is not bound: A_Type_Access (access to classwide type) not yet supported.
        // The calls below show the desired behaviour once the feature is implemented.
        TestPackage.setX(true, v);
        System.out.println(TestPackage.x().getF());
    }

}
