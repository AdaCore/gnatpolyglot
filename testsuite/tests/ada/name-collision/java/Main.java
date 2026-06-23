import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.system.Foo;
import com.adacore.libtest.system.Address;
import com.adacore.libtest.test.int_.IntPackage;

public class Main {

    public static void main(String[] args) throws Throwable {
        TestPackage.void_();
        Address address = new Address();
        TestPackage.delete(address);

        Foo f = new Foo();
        TestPackage.foo(address);

        IntPackage.int_(1);
    }
}
