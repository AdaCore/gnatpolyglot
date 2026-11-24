import com.adacore.libtest.test.Child;

// F(Child) return Integer and F(Child) return Float — overloaded by return type.
// Proxy compilation fails (name mismatch in abstract/concrete hierarchy).
// Also tagged types not yet supported in Java printer.

public class Main {
    public static void main(String[] args) {
        Child c = new Child();
        System.out.println(c.f());
    }
}
