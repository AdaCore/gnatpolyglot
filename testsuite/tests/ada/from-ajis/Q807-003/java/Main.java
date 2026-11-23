import com.adacore.libtest.definitions.AType;
import com.adacore.libtest.definitions.DefinitionsPackage;
import com.adacore.libtest.users.UsersPackage;

public class Main {

    public static void main(String[] args) {
        AType t0 = new AType();
        AType t2 = DefinitionsPackage.create(2);
        if (t0.equals(DefinitionsPackage.getNullValue())
                && t2.equals(UsersPackage.getRealValue())) {
            System.out.println("OK");
        }
    }
}
