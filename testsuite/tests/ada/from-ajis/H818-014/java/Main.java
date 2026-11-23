import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Type1;
import com.adacore.libtest.test.Type2;

public class Main {

    static class CppType1 extends Type1 {
        @Override
        public void somePrimitive() {
            System.out.println("some_primitive called");
        }
    }

    static class CppType2 extends Type2 {
        @Override
        public Type1 get() {
            return new CppType1();
        }
    }

    public static void main(String[] args) {
        // create_and_free dispatches V.Get (abstract) then calls some_primitive
        TestPackage.createAndFree(new CppType2());
    }

}
