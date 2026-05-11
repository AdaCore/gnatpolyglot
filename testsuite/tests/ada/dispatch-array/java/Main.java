import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Root;
import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;

public class Main {

    public static void print(IntegerArray a) {
        for (int i = 0; i < a.size(); i++) {
            System.out.print(a.get(i) + ", ");
        }
        System.out.println();
    }

    public static class Child extends Root {

        public IntegerArray f(IntegerArray a) {
            System.out.println("Child.f()");
            IntegerArray res = a.clone();

            for (int i = 0; i < a.size(); i++)
                a.set(i, a.get(i) + 2);

            return res;
        }

    }

    public static void main(String[] args) throws Throwable {
        Root root = new Root();
        IntegerArray a = new IntegerArray(1, 10);
        for (int i = a.getBegin(); i <= a.getEnd(); i++)
            a.setUnslided(i, i);
        print(a);
        print(TestPackage.callF(root, a));
        print(a);

        Child child = new Child();
        print(TestPackage.callF(child, a));
        print(a);
    }
}
