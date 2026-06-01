import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.T1;

public class Main {

    static class JavaType extends T1 {
        @Override
        public void overloadedProc() {
            System.out.println("Hello from Java");
        }

        @Override
        public void overloadedProc(int a) {
            System.out.println("Hello from Java: " + a);
        }
    }

    public static void main(String[] args) throws Throwable {
        T1 t = new T1();
        JavaType java = new JavaType();

        TestPackage.callOverload(t);
        TestPackage.callOverload(t, 1);
        TestPackage.callOverload(java);
        TestPackage.callOverload(java, 2);
    }
}
