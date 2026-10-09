import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.T;
import com.adacore.libtest.test.nested.NestedPackage;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws Throwable {
        Optional<T> tclass = TestPackage.testTClassMake();
        T t = TestPackage.testTMake();

        NestedPackage.foo(new T());
        NestedPackage.foo1(new T());

        assert TestPackage.operatorEq(new T(), new T());
    }
}
