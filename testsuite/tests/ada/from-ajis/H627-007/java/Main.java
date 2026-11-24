import com.adacore.libtest.test.A;
import com.adacore.libtest.test.B;

public class Main {

    public static void main(String[] args) {
        A v1 = new A();
        B v2 = new B();
        v1 = v1.incrementAll();
        v2 = v2.incrementAll();
        v1.print();
        v2.print();
    }
}
