import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.T;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

public class Main {

    public static class Child extends T {
        @Override
        public PolyglotString concat(PolyglotString a, PolyglotString b) {
            return new PolyglotString(
                a.toString() + b.toString() + a.toString()
            );
        }
    }

    public static void main(String[] args) throws Throwable {
        T obj = new T();
        Child c = new Child();

        System.out.println(
            TestPackage.callConcat(
                obj,
                new PolyglotString("foo"),
                new PolyglotString("bar")
            ).toString()
        );
        System.out.println(
            TestPackage.callConcat(
                c,
                new PolyglotString("bar"),
                new PolyglotString("baz")
            ).toString()
        );
    }
}
