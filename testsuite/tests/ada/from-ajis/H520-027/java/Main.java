import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.AnArray;
import com.adacore.libtest.test.Enum;

public class Main {
    public static void main(String[] args) {
        AnArray arr = new AnArray();
        arr.setElementAt(Enum.A, 90);
        System.out.println("VALUE OF A IS " + arr.getElementAt(Enum.A));
    }
}
