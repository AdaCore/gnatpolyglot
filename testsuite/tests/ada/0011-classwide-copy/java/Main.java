import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Root;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static class Inherited extends Root {

        public Inherited() {
            super(1, 2);
        }

        public List<Integer> lst = new ArrayList<>(List.of(1, 2, 3));

        @Override
        public void p() {
            System.out.println(lst);
            super.p();
        }

        @Override
        public void p2() {
            super.p2();
            for (int i = 0; i < lst.size(); i++) {
                lst.set(i, lst.get(i) * 2);
            }
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            Inherited copy = (Inherited) super.clone();
            System.out.println("Java clone");
            copy.lst = new ArrayList<>(lst);
            return copy;
        }
    }

    public static class MissingClone extends Root {
        public MissingClone() {
            super(1, 2);
        }

        public List<Integer> lst = new ArrayList<>(List.of(1, 2, 3));

        @Override
        public void p() {
            System.out.println(lst);
            super.p();
        }

        @Override
        public void p2() {
            super.p2();
            for (int i = 0; i < lst.size(); i++) {
                lst.set(i, lst.get(i) * 2);
            }
        }

    }

    public static void main(String[] args) throws Throwable {
        Inherited in = new Inherited();
        in.p();
        TestPackage.pMakeCopy(in);

        // Java permits a form of polymorphic copy through its "clone" function.
        // When not overridden by the class, it shall permorm a shallow copy by
        // default, so there is not error to catch in this case.
        TestPackage.pMakeCopy(new MissingClone());

        Inherited in2 = (Inherited) in.clone();
        assert in._getData().getAddress() != in2._getData().getAddress();

        Root r = new Root(1, 2);
        assert r._getData().getAddress() != ((Root) r.clone())._getData().getAddress();
    }
}
