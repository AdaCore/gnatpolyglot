import java.util.Optional;

import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Type1;
import com.adacore.libtest.test.Type2;

public class Main {
    static class Type1Java extends Type1 {}

    static class Type2Java extends Type2 {
        @Override
        public Optional<Type1> get() {
            return Optional.of(new Type1Java());
        }
    }

    static class Type2JavaNull extends Type2 {
        @Override
        public Optional<Type1> get() {
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        TestPackage.callGet(new Type2Java());
        System.out.println("end of main");
    }
}
