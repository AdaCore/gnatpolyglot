import com.adacore.libtest.test.TestPackage;

// "/" is abstract (not bound). "*" is bound as operatorMult.

public class Main {
    public static void main(String[] args) {
        System.out.println(TestPackage.operatorMult(0, 0));
    }
}
