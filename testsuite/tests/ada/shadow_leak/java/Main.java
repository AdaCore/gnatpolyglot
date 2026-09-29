import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.test.T;

public class Main {
    public static class Child extends T {}

    public static void main(String[] args) throws Throwable {
        T v = new Child();
        v._setOwner(Owner.LIBRARY);
        System.out.println("end of main");
    }
}
