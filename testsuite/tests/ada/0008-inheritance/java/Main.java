import com.adacore.libtest.test.Child;
import com.adacore.libtest.test.Rec;
import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.TestPackage;
import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;
import com.adacore.gnatpolyglot.runtime.IntegerRef;

import java.util.List;

public class Main {

    public static class OtherChild extends Root {

        private String str;

        public OtherChild(String str) {
            super(4, 2);
            this.str = str;
        }

        @Override
        public void p1() {
            System.out.println(str);
        }

        @Override
        public Rec f() {
            return new Rec(21);
        }

        @Override
        public IntegerArray fArr(IntegerArray a, IntegerRef i) {
            IntegerArray res = new IntegerArray(a.getBegin(), a.getEnd());
            for (int index = 0; index < a.size(); index++) {
                res.set(index, a.get(index) + 2);
            }
            System.out.print("Returning from Java: ");
            for (int index = 0; index < res.size(); index++) {
                System.out.format("%d, ", res.get(index));
            }
            System.out.println();
            i.setValue(i.getValue() + 2);
            return res;
        }
    }

    public static class GrandChild extends Child {

        List<Integer> vec;

        public GrandChild(List<Integer> data) {
            super(4, 2, 1);
            this.vec = data;
        }

        @Override
        public void p1() {
            for (var i : vec) {
                System.out.print(i + " ");
            }
            System.out.println();
        }

        @Override
        public void p2(Rec r, int i) {
            System.out.format("P2 from Java %d: %d\n", i, r.getI());
        }

        @Override
        public Rec f() {
            return new Rec(100);
        }
    }

    public static void testArray(Root r, IntegerArray input) {
        IntegerRef i = new IntegerRef(0);
        IntegerArray res = TestPackage.fArrDisp(r, input, i);
        System.out.print("Got in Java: ");
        for (int index = 0; index < res.size(); index++) {
            System.out.format("%d, ", res.get(index));
        }
        System.out.println();
        System.out.format("i = %d\n", i.getValue());
    }

    public static void main(String[] args) {
        Root root = new Root(1, 2);
        root.p1();

        Child child = new Child(3, 4, 5);
        child.p1();

        OtherChild other = new OtherChild("Java inherited");
        other.p1();

        GrandChild grand = new GrandChild(List.of(3, 4, 5));
        grand.p1();

        TestPackage.pRoot(root);
        TestPackage.pRoot(child);
        TestPackage.pRoot(other);
        TestPackage.pRoot(grand);

        Rec r1 = TestPackage.fRec(root);
        System.out.format("r1 = %d\n", r1.getI());
        Rec r2 = TestPackage.fRec(other);
        System.out.format("r2 = %d\n", r2.getI());
        Rec r3 = TestPackage.fRec(grand);
        System.out.format("r3 = %d\n", r3.getI());

        TestPackage.p2Child(child, r1, 1);
        TestPackage.p2Child(grand, r2, 1);

        IntegerArray arr = new IntegerArray(1, 5);
        for (int i = arr.getBegin(); i <= arr.getEnd(); i++) {
            arr.setUnslided(i, i);
        }
        testArray(root, arr);
        testArray(other, arr);
    }
}
