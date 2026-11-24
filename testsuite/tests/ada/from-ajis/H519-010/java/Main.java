import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        // An_Enum_Array (enum-indexed) and V are not bound.
        // Sum and F (ordinary array functions) may be callable.
        var arr = TestPackage.f();
        System.out.println("Sum = " + TestPackage.sum(arr));
    }
}
