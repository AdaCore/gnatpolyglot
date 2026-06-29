import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Vector;
import com.adacore.libtest.test.InlineVector;

import java.util.Optional;

import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;

public class Main {

    static void print(IntegerArray arr) {
        for (int i = 1; i <= arr.size(); i++)
            System.out.print(arr.getUnslided(i) + ", ");
        System.out.println();
    }

    static void print(InlineVector vec) {
        for (int i = 1; i <= vec.getSize(); i++)
            System.out.print(vec.get(i) + ", ");
        System.out.println();
    }

    static void print(Vector vec) {
        for (int i = 1; i <= vec.getSize(); i++)
            System.out.print(vec.get(i) + ", ");
        System.out.println();
    }

    static void inlineVec() {
        System.out.println("InlineVector");
        InlineVector v = new InlineVector();
        IntegerArray arr = v.getData();
        v.push(1);
        v.push(2);
        v.push(3);
        print(v);
        print(arr);
        v.push(4);
        v.push(5);
        v.push(6);
        print(v);
        print(arr);
        v.pop();
        v.pop();
        v.pop();
        v.push(7);
        v.push(8);
        v.push(9);
        print(v);
        print(arr);
    }

    static void vec() {
        System.out.println("Vector");
        Vector v = new Vector();
        Optional<IntegerArray> arr = v.getData();
        assert arr.isEmpty();
        v.push(1);
        v.push(2);
        v.push(3);
        print(v);
        arr = v.getData();
        print(arr.get());
        v.push(4);
        v.push(5);
        v.push(6);
        print(v);
        arr = v.getData();
        print(arr.get());
        v.pop();
        v.pop();
        v.pop();
        v.push(7);
        v.push(8);
        v.push(9);
        print(v);
        arr = v.getData();
        print(arr.get());
        v.free();
    }

    public static void main(String[] args) throws Throwable {
        inlineVec();
        vec();
    }
}
