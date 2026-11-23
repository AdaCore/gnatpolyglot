import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Enum;
import com.adacore.libtest.test.AnUnconstrainedArray;
import com.adacore.libtest.test.AConstrainedArray;

public class Main {

    public static void main(String[] argv) {
        AnUnconstrainedArray arr1 = new AnUnconstrainedArray(Enum.A, Enum.B);
        AConstrainedArray arr2 = new AConstrainedArray();

        arr1.setElementAt(Enum.A, 'A');
        arr1.setElementAt(Enum.B, 'B');

        arr2.setElementAt(Enum.A, 'a');
        arr2.setElementAt(Enum.B, 'b');
        arr2.setElementAt(Enum.C, 'c');
        arr2.setElementAt(Enum.D, 'd');
        arr2.setElementAt(Enum.E, 'e');
        arr2.setElementAt(Enum.F, 'f');

        TestPackage.display(arr1, arr2);
    }

}
