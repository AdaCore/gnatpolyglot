import com.adacore.libtest.p.x.T;
import com.adacore.libtest.p.x.XPackage;

// Proc_2 not bound: anonymous access T parameter not yet supported.
// Proxy compilation also fails (bug in proxy generator for child package with global named P).

public class Main {
    public static void main(String[] args) {
        T val = new T();
        val.proc();
    }
}
