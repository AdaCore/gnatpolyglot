import java.util.Optional;

import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
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

    static class Type2JavaLibrary extends Type2 {
        @Override
        public Optional<Type1> get() {
            return Optional.of(new Type1Java())
                .map(obj -> {
                    obj._setOwner(Owner.LIBRARY);
                    return obj;
                });
        }
    }

    public static void main(String[] args) {
        TestPackage.callGet(new Type2Java());
        try {
            TestPackage.callGet(new Type2JavaNull());
        } catch (Throwable t) {
            System.out.println("caught: " + t.getMessage());
        }
        try {
            TestPackage.callGet(new Type2JavaLibrary());
        } catch (Throwable t) {
            System.out.println("caught: " + t.getMessage());
        }
        System.out.println("end of main");
    }
}
