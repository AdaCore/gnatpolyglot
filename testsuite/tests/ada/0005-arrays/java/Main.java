import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.MyInt;
import com.adacore.gnatpolyglot.runtime.ada2java.*;

public class Main {

    static void testIntegerArrays() {
        System.out.println("-- integer arrays --");

        // A function returning an unconstrained array.
        IntegerArray arr = TestPackage.fU1();
        System.out.println("bounds: " + arr.getBegin() + " " + arr.getEnd());
        System.out.println("content: [" + arr.getUnslided(1) + " "
            + arr.getUnslided(2) + " " + arr.getUnslided(3) + "]");
        System.out.println("sum: " + TestPackage.fU2(arr));

        // Write elements back, then read them through a parameter.
        arr.setUnslided(1, 4);
        arr.setUnslided(2, 5);
        arr.setUnslided(3, 6);
        System.out.println("content: [" + arr.getUnslided(1) + " "
            + arr.getUnslided(2) + " " + arr.getUnslided(3) + "]");
        System.out.println("sum: " + TestPackage.fU2(arr));

        // An `in out` parameter: Ada mutates the shared buffer in place.
        TestPackage.outProc(arr);
        System.out.print("doubled:");
        for (var x : arr) {
            System.out.print(" " + x);
        }
        System.out.println();

        // An array allocated and filled on the Java side.
        IntegerArray owned = new IntegerArray(1, 5);
        int v = 0;
        for (int i = 0; i < owned.size(); i++) {
            owned.set(i, v += 2);
        }
        System.out.print("owned:");
        for (var x : owned) {
            System.out.print(" " + x);
        }
        System.out.println();
    }

    static void testIntegerWidths() {
        System.out.println("-- integer widths --");

        ByteArray bytes = TestPackage.makeBytes();
        System.out.print("bytes:");
        for (var x : bytes) {
            System.out.print(" " + x);
        }
        System.out.println();

        ShortArray shorts = TestPackage.makeShorts();
        System.out.print("shorts:");
        for (var x : shorts) {
            System.out.print(" " + x);
        }
        System.out.println();

        // The 64-bit values exceed 32 bits, proving the elements are not
        // truncated.
        LongArray longs = TestPackage.makeLongs();
        System.out.print("longs:");
        for (var x : longs) {
            System.out.print(" " + x);
        }
        System.out.println();
    }

    static void testFloatingPointArrays() {
        System.out.println("-- floating-point arrays --");

        FloatArray floats = TestPackage.makeFloats();
        System.out.print("floats:");
        for (var x : floats) {
            System.out.print(" " + x);
        }
        System.out.println();
        System.out.println("sum: " + TestPackage.sumFloats(floats));

        // An `in out` parameter mutates the shared buffer in place.
        TestPackage.scaleFloats(floats);
        System.out.print("scaled:");
        for (var x : floats) {
            System.out.print(" " + x);
        }
        System.out.println();
        System.out.println("sum: " + TestPackage.sumFloats(floats));

        // A float array allocated on the Java side.
        FloatArray owned = new FloatArray(1, 3);
        owned.setUnslided(1, 0.25f);
        owned.setUnslided(2, 0.5f);
        owned.setUnslided(3, 0.75f);
        System.out.print("owned:");
        for (var x : owned) {
            System.out.print(" " + x);
        }
        System.out.println();

        DoubleArray doubles = TestPackage.makeDoubles();
        System.out.print("doubles:");
        for (var x : doubles) {
            System.out.print(" " + x);
        }
        System.out.println();
        System.out.println("sum: " + TestPackage.sumDoubles(doubles));
    }

    static void testBooleanArrays() {
        System.out.println("-- boolean arrays --");

        BooleanArray bools = TestPackage.makeBools();
        System.out.print("bools:");
        for (var x : bools) {
            System.out.print(" " + x);
        }
        System.out.println();
        System.out.println("any: " + TestPackage.anyTrue(bools));

        // An `in out` parameter flips each element in place.
        TestPackage.negateBools(bools);
        System.out.print("negated:");
        for (var x : bools) {
            System.out.print(" " + x);
        }
        System.out.println();
        System.out.println("any: " + TestPackage.anyTrue(bools));

        // A boolean array allocated on the Java side.
        BooleanArray owned = new BooleanArray(1, 2);
        owned.setUnslided(1, true);
        owned.setUnslided(2, false);
        System.out.print("owned:");
        for (var x : owned) {
            System.out.print(" " + x);
        }
        System.out.println();
    }

    static void testRecordArrays() {
        System.out.println("-- record arrays --");

        MyInt.Array arr = TestPackage.myIntArrFunc();
        System.out.println("bounds: " + arr.getBegin() + " " + arr.getEnd());
        System.out.print("content:");
        for (var el : arr) {
            System.out.print(" " + el.getI());
        }
        System.out.println();

        // An `in out` parameter mutates the records in place.
        TestPackage.myIntArrProc(arr);
        System.out.print("tripled:");
        for (var el : arr) {
            System.out.print(" " + el.getI());
        }
        System.out.println();
        TestPackage.printImage(arr);

        // A record array allocated and filled on the Java side.
        MyInt.Array owned = new MyInt.Array(1, 3);
        for (int i = 0; i < owned.size(); i++) {
            owned.set(i, new MyInt((i + 1) * 10));
        }
        System.out.print("owned:");
        for (var el : owned) {
            System.out.print(" " + el.getI());
        }
        System.out.println();
    }

    public static void main(String[] args) throws Throwable {
        testIntegerArrays();
        testIntegerWidths();
        testFloatingPointArrays();
        testBooleanArrays();
        testRecordArrays();
    }
}
