import com.adacore.libtest.binded.BindedPackage;
import com.adacore.libtest.test.child.T;

public class Main {

    public static void main(String[] args) throws Throwable {
        T t = new T();
        BindedPackage.p(t);
    }
}
