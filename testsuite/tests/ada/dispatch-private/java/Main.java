import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.Priv;

public class Main {

    public static class Child extends Root {
        public Priv f(Priv p) {
            System.out.println("Main.Child.f()");
            return p.inc();
        }
    }

    public static void main(String[] args) throws Throwable {
        Root root = new Root();
        Priv priv = new Priv();
        priv.print();
        TestPackage.callF(root, priv).print();

        Child child = new Child();
        TestPackage.callF(child, priv).print();

    }
}
