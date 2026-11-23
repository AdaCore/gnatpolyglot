import com.adacore.libtest.test.Root1;
import com.adacore.libtest.test.Root2;

public class Main {

    class C1 extends Root1 {
        @Override
        public void p() {
            System.out.println("P of C1");
        }
    }

    class C2 extends Root2 {
        @Override
        public void p() {
            System.out.println("P of C2");
        }
    }

    public void run() {
        (new Root1()).p();
        (new C1()).p();
        (new C2()).p();
    }

    public static void main(String[] args) {
        Main v = new Main();
        v.run();
    }
}
