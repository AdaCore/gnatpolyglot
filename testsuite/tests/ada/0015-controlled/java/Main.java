import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.nested.NestedPackage;
import com.adacore.libtest.test.Cont;
import com.adacore.libtest.test.ContWrapper;

public class Main {

    static class InheritsControlled extends Cont { }

    public static void main(String[] args) throws Throwable {
        try (
            Cont cont = new Cont();
            Cont cont2 = new Cont()
        ) {
            cont.copyFrom(cont2);
            try (Cont foo = new Cont(cont)) {
                cont.p();
                cont2.p();
                foo.p();
            }
        }
        System.out.println();

        try(
            InheritsControlled cont = new InheritsControlled();
        ) {
            cont.p();
        }
        System.out.println();

        try(
            Cont cont = new Cont();
            ContWrapper wrapper = new ContWrapper(cont);
            ContWrapper cont2 = new ContWrapper();
        ) {
            cont.p();
            wrapper.getC().p();
            cont2.getC().p();
        }
        System.out.println();

        try(
            Cont.Array arr = TestPackage.getArr();
        ) {
            TestPackage.foo(arr);
            TestPackage.foo(arr);
            TestPackage.foo(arr);
        }

        try(
            Cont c1 = new Cont();
            Cont c2 = new Cont()
        ) {
            System.out.println(c1.operatorEq(c2));
        }
        System.out.println();

        try(
            Cont cont = NestedPackage.getCont();
            Cont cont2 = new Cont(cont);
        ) {
            cont.p();
            cont2.p();
        }
    }
}
