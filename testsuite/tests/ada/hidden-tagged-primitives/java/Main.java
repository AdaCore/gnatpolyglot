import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.B;
import com.adacore.libtest.test.C;
import com.adacore.libtest.test.E;

public class Main {

    public static void main(String[] args) throws Throwable {
        B b = new B();
        b.foo();
        C c = new C();
        c.foo();
        c.bar();
        E e = new E();
        e.foo();
    }
}
