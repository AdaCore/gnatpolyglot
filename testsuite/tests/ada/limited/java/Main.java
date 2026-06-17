import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Value;
import com.adacore.libtest.test.TaggedValue;

import java.lang.Cloneable;

public class Main {

    public static class Child extends TaggedValue {

        int i = 4;

        @Override
        public void p() {
            super.p();
            System.out.println(i += 2);
        }
    }

    public static void main(String[] args) throws Throwable {
        try {
            Value.class.getConstructor(Value.class);
            System.out.println("found");
        } catch (NoSuchMethodException e) {}
        // PolyglotObject defines the method but it remains protected. Only
        // copyable object will expose it as public.
        assert !Value.class.getMethod("clone").canAccess(Main.class);
        assert !Cloneable.class.isAssignableFrom(Value.class);

        Value v = TestPackage.initValue(3, 4);
        v.p();
        v.p();

        TaggedValue x = new TaggedValue();
        x.p();
        x.p();

        Child y = new Child();
        y.p();

        TestPackage.getGlobal().p();
        TestPackage.getGlobal().p();
    }
}
