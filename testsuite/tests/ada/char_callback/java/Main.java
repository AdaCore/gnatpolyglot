import com.adacore.gnatpolyglot.runtime.CharacterRef;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        TestPackage.call((CharacterRef v1, CharacterRef v2) -> {
            System.out.println("Java: " + v2.getValue());
            v1.setValue('w');
            v2.setValue('e');
        });
    }
}
