import com.adacore.libex2.example.ExamplePackage;
import com.adacore.libex2.example.Counter;

import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;

public class Main {
    public static void main(String[] args) {
        Counter.Array arr = new Counter.Array(1, 4);
        for (var c : arr)
            c.increment();
        for (var c : arr)
            System.out.print(c.getCount() + ", ");
        System.out.println();

        ExamplePackage.increment(arr);
        for (var c : arr)
            System.out.print(c.getCount() + ", ");
        System.out.println();

        IntegerArray intArr = ExamplePackage.toIntArr(arr);
        for (var i : intArr)
            System.out.print(i + ", ");
        System.out.println();
    }
}
