import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.ATaggedType;

public class Main {

    static class AJavaChild extends ATaggedType {
        public AJavaChild(int a, int b, PolyglotString s1, PolyglotString s2) {
            super(a, b, s1, s2);
        }

        @Override
        public void printStrings() {
            super.printStrings();

            System.out.println("FROM JAVA: [" + getS1() + "], [" + getS2() + "]");
        }
    }

    public static void main(String[] args) {
        ATaggedType v1, v2;

        v1 = new ATaggedType(5, 2, new PolyglotString("     "), new PolyglotString("  "));
        v1.setS1(new PolyglotString("12345"));
        v1.setS2(new PolyglotString("67"));

        v2 = new AJavaChild(9, 1, new PolyglotString("         "), new PolyglotString(" "));
        v2.setS1(new PolyglotString("ABCDEFGHI"));
        v2.setS2(new PolyglotString("J"));

        v1.printStrings();
        v2.printStrings();
    }
}
