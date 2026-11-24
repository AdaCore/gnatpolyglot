import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.cont.R;

public class Main {

    static class Child extends Root {
        int x = 0;
    }

    public static void main(String[] args) {
        Child c = new Child();
        c.x = 50;

        R v = new R();

        v.set(c);

        System.out.println(((Child) v.get().get()).x);
    }
}
