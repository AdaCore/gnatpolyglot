import com.adacore.libtest.test.TestPackage;
import com.adacore.gnatpolyglot.runtime.Functions.Function2;
import com.adacore.libtest.test.T;
import com.adacore.libtest.test.Rec;

public class Main {

    static class Child extends T {
        @Override
        public int tCall(com.adacore.libtest.Callbacks.Callback0 c) {
            return c.apply(-1, -2);
        }
    }

    static int foo(int i, int j) {
        System.out.println("Hello!");
        return i + j;
    }

    public static void main(String[] args) throws Throwable {
        {
            // Scalar callbacks
            System.out.println(TestPackage.call(Main::foo));
            System.out.println(TestPackage.call((i, j) -> i * j));
            // Record callbacks
            Rec r = TestPackage.call(
                (Rec rec) -> new Rec(rec.getI() - 1, rec.getJ() - 2)
            );
            System.out.println("Rec{%d, %d}".formatted(r.getI(), r.getJ()));
            // Array callbacks
            Rec.Array arr = TestPackage.call(
                (Rec.Array a) -> {
                    Rec.Array res = new Rec.Array(a);
                    for (var rec : res) {
                        rec.setI(rec.getI() + 4);
                        rec.setJ(rec.getJ() + 8);
                    }
                    return res;
                }
            );
            System.out.println(
                "{Rec{%d, %d}, Rec{%d, %d}}".formatted(
                    arr.get(0).getI(), arr.get(0).getJ(),
                    arr.get(1).getI(), arr.get(1).getJ()
                )
            );
        }

        System.out.println();

        {
            // Scalar callbacks
            System.out.println(TestPackage.getCallback1().apply(10, 8));
            // Record callbacks
            Rec r = TestPackage.getCallback2().apply(new Rec(10, 8));
            System.out.println("Rec{%d, %d}".formatted(r.getI(), r.getJ()));
            // Array callbacks
            Rec.Array arr1 = new Rec.Array(1, 2);
            arr1.set(0, new Rec(1, 2));
            arr1.set(1, new Rec(3, 4));
            Rec.Array arr2 = TestPackage.getCallback3().apply(arr1);
            System.out.println(
                "{Rec{%d, %d}, Rec{%d, %d}}".formatted(
                    arr2.get(0).getI(), arr2.get(0).getJ(),
                    arr2.get(1).getI(), arr2.get(1).getJ()
                )
            );
        }

        System.out.println();

        com.adacore.libtest.Callbacks.Callback0.Ref f =
            new com.adacore.libtest.Callbacks.Callback0.Ref();
        TestPackage.getCallback(f);
        System.out.println(f.get().apply(10, 8));

        T t = new T();
        Child c = new Child();
        System.out.println(TestPackage.callTCall(t, Main::foo));
        System.out.println(TestPackage.callTCall(c, Main::foo));
    }
}
