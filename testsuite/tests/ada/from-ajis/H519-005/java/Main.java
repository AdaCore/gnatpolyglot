import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        String globalString = TestPackage.returnAStringAccess();

        String otherString = "This string is coming from Java";

        TestPackage.displayAStringAccess(globalString);
        TestPackage.displayAStringAccess(otherString);
    }
}
