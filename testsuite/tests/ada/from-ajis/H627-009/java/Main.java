import com.adacore.libtest.base.AReturnedType;
import com.adacore.libtest.test.AType;

public class Main {
    static class AJavaType extends AType {
        @Override
        public AReturnedType getSomething(AReturnedType x) {
            return x;
        }
    }

    public static void main(String[] args) {
        AType t = new AJavaType();
        t.getSomething(new AReturnedType());

        //  This doesn't actually do anything, but should work without
        //  compilation errors.
    }
}
