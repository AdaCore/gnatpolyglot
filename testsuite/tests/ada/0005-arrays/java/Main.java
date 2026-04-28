import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.MyInt;
import com.adacore.gnatpolyglot.runtime.ada2java.*;

public class Main {

    public static void testNativeArrays() {
        System.out.println("Starting testNativeArrays");

        IntegerArray arr = TestPackage.fU1();
        System.out.format("bounds:%d %d\n", arr.getBegin(), arr.getEnd());
        System.out.format(
            "content : [%d, %d, %d]\n",
            arr.getUnslided(1),
            arr.getUnslided(2),
            arr.getUnslided(3)
        );
        System.out.format("total: %d\n", TestPackage.fU2(arr));

        System.out.println("setting new value");
        arr.setUnslided(1, 4);
        arr.setUnslided(2, 5);
        arr.setUnslided(3, 6);
        System.out.format("content : [%d, %d, %d]\n", arr.get(0), arr.get(1), arr.get(2));
        System.out.format("total: %d\n", TestPackage.fU2(arr));

        System.out.println("==================");

        System.out.print("arr: ");
        for (int i = arr.getBegin(); i <= arr.getEnd(); i++) {
            System.out.format("%d, ", arr.getUnslided(i));
        }
        System.out.println();

        System.out.println("Calling out array param procedure...");
        TestPackage.outProc(arr);
        System.out.print("arr: ");
        for (int i = 0; i < arr.size(); i++) {
            System.out.format("%d, ", arr.get(i));
        }
        System.out.println();

        System.out.println("Done.");
    }

    public static void testStructArrays() {
        System.out.println("Starting testStructArrays");

        MyInt.Array arr = TestPackage.myIntArrFunc();
        System.out.format("bounds:%d %d\n", arr.getBegin(), arr.getEnd());
        System.out.println("==================");
        System.out.print("arr: ");
        for (int i = 0; i < arr.size(); i++) {
            System.out.format("%d, ", arr.get(i).getI());
        }
        System.out.println();

        System.out.println("Calling out array param procedure...");
        TestPackage.myIntArrProc(arr);
        System.out.print("arr: ");
        for (int i = 0; i < arr.size(); i++) {
            System.out.format("%d, ", arr.get(i).getI());
        }
        System.out.println();

        TestPackage.printImage(arr);

        System.out.println("Done.");
    }

    public static void foreachLoop() {
        {
            IntegerArray arr = new IntegerArray(1, 10);
            int n = 0;
            for (int i = 0; i < arr.size(); i++) {
                arr.set(i, n += 2);
            }
            for (var i : arr) {
                System.out.format("%d, ", i);
            }
            System.out.println();
        }
        {
            MyInt.Array arr = new MyInt.Array(1, 10);
            int n = 0;
            for (int i = 0; i < arr.size(); i++) {
                arr.set(i, new MyInt(n += 2));
            }
            for (var i : arr) {
                System.out.format("%d, ", i.getI());
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws Throwable {
        testNativeArrays();
        System.out.println("============================================");
        testStructArrays();
        System.out.println("============================================");
        foreachLoop();
    }
}
