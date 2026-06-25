import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Rec;
import com.adacore.libtest.lists.List;
import com.adacore.libtest.lists.ListItem;

import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class Main {

    static void simpleRef() {
        Rec rec = new Rec(1);

        rec.setOwner(Owner.LIBRARY);
        Rec other = TestPackage.recF(rec).get();

        System.out.println(rec.getI());
        System.out.println(other.getI());

        TestPackage.recP(other);
        System.out.println(other.getI());
        TestPackage.recP(null);
        System.out.println(other.getI());
        TestPackage.recP(null);
        System.out.println(other.getI());

        try {
            other.setOwner(Owner.USER);
            TestPackage.recP(other);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(other.getI());
        other.setOwner(Owner.LIBRARY);
        Rec.Ref ref = new Rec.Ref(other);
        TestPackage.recInOut(ref);
        System.out.println(ref.get().get().getI());

        rec.setOwner(Owner.USER);
        other.setOwner(Owner.USER);
    }

    static void useFree() {
        Rec.Ref ptr1 = new Rec.Ref(new Rec(1));
        ptr1.get().get().setOwner(Owner.LIBRARY);
        TestPackage.free(ptr1);
    }

    static void recursiveRec() {
        List list = new List((ListItem) null);
        ListItem item1 = new ListItem(1);
        item1.setOwner(Owner.LIBRARY);
        ListItem item2 = new ListItem(2);
        item2.setOwner(Owner.LIBRARY);

        list.push(item1);
        System.out.println(list.get(0).get().getValue());

        list.push(item2);
        System.out.println(list.get(1).get().getValue());
        System.out.println(item1.getNext().get().getNext());

        ListItem item3 = new ListItem(3);
        item3.setOwner(Owner.LIBRARY);
        item2.setNext(item3);
        System.out.println(list.get(2).get().getValue());

        item1.setOwner(Owner.USER);
        item2.setOwner(Owner.USER);
        item3.setOwner(Owner.USER);
    }

    static void printArray(IntegerArray arr) {
        System.out.print("{ ");
        for (var i : arr) {
            System.out.print(i + ", ");
        }
        System.out.println("}");
    }

    static void array() {
        IntegerArray arr = new IntegerArray(1, 4);
        for (int i = arr.getBegin(); i <= arr.getEnd(); i++) {
            arr.setUnslided(i, i);
        }
        arr.setOwner(Owner.LIBRARY);

        IntegerArray other = TestPackage.arrF(arr).get();
        printArray(arr);
        printArray(other);

        TestPackage.arrP(arr);
        TestPackage.arrP(arr);
        printArray(arr);
        printArray(other);

        TestPackage.arrP(null);

        IntegerArray.Ref ref = new IntegerArray.Ref(other);
        TestPackage.arrPInOut(ref);
        printArray(arr);
        printArray(ref.get().get());
        arr.setOwner(Owner.USER);
        other.setOwner(Owner.USER);
    }

    public static void main(String[] args) throws Throwable {
        simpleRef();
        useFree();
        System.out.println();
        recursiveRec();
        System.out.println();
        array();
    }
}
