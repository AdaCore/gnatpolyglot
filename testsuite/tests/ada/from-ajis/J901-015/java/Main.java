import com.adacore.libtest.test.T;
import com.adacore.libtest.test.R;
import com.adacore.libtest.test.TestPackage;

// T is abstract tagged; tagged types not yet supported in Java printer.
// Ptr_F, Ptr_P, Call_F, Call_P also not bound (access-to-subprogram).

public class Main {
    static class MyT extends T {
        @Override
        public R f1() { return new R(1, 2); }

        @Override
        public R f2() { return new R(3, 4); }

        @Override
        public void p(R[] x) { x[0] = new R(5, 6); }
    }

    public static void main(String[] args) {
        MyT t = new MyT();
        TestPackage.testAll(t);
    }
}
