import com.adacore.libtest.floats.FloatsPackage;
import com.adacore.libtest.floats.T;

public class Main {

    static class Child extends T {
        public float inc(float f) {
            return f + 2;
        }
    }

    public static void main(String[] args) throws Throwable {
        T obj = new T();
        Child c = new Child();

        System.out.println(FloatsPackage.callInc(obj, 2.5f));
        System.out.println(FloatsPackage.callInc(c, 2.5f));
    }
}
