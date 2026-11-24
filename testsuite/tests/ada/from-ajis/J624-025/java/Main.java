import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.OtherRoot;

// P not bound: anonymous access Other_Root'Class parameter not yet supported.
// Also abstract tagged types not yet supported in Java printer.

public class Main {
    static class ConcreteRoot extends Root {}
    static class ConcreteOther extends OtherRoot {}

    public static void main(String[] args) {
        Root r = new ConcreteRoot();
        OtherRoot o = new ConcreteOther();
        r.p(o);
        System.out.println("OK");
    }
}
