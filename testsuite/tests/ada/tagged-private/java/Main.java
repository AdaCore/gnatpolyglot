import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Root;

public class Main {

    static class Child extends Root {

        int i;

        Child(int i) {
            this.i = i;
        }

        @Override
        public void p1() {
            i += 1;
            System.out.println("i = " + i);
        }

    }

    public static void main(String[] args) throws Throwable {
        Root root = new Root();
        root.p1();
        System.out.println();

        Child c = new Child(3);
        c.p1();
        System.out.println();
    }
}
