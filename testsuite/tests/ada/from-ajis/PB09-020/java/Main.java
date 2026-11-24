import com.adacore.libtest.hello.EditorBuffer;
import com.adacore.libtest.hello.T1;
import com.adacore.libtest.hello.T2;

public class Main {

    private static class Buffer extends EditorBuffer {
        @Override
        public int f1() {
            return 1;
        }
    }

    private static class MyT2 extends T2 {}

    public static void main(String[] args) {
        T1 t1 = new T1();
        Buffer buffer = new Buffer();
        MyT2 t2 = new MyT2();
        System.out.println(buffer.f1() + " " + t1.f4() + " " + t2.f7());
    }
}
