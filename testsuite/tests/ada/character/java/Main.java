import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.T;
import com.adacore.gnatpolyglot.runtime.CharacterRef;

public class Main {

    public static class Child extends T {

        public char tF(char e) {
            return switch (e) {
                case 'a' -> 'j';
                case 'b' -> 'e';
                default -> 'f';
            };
        }

    }

    public static void main(String[] args) throws Throwable {
        char e1 = 'A';
        TestPackage.pCharacter(e1);
        e1 = TestPackage.fCharacter(e1);
        TestPackage.pCharacter(e1);
        System.out.println("Java value: " + e1);

        char e2 = 'e';
        TestPackage.pCharacter(e2);
        e2 = TestPackage.fCharacter(e2);
        TestPackage.pCharacter(e2);
        System.out.println("Java value: " + e2);

        CharacterRef ref = new CharacterRef(e2);
        TestPackage.pInOut(ref);
        System.out.println("Java value: " + ref.getValue());

        Child c = new Child();
        System.out.println(TestPackage.callTF(c, 'a'));
        System.out.println(TestPackage.callTF(c, 'b'));
        System.out.println(TestPackage.callTF(c, 'c'));
    }
}
