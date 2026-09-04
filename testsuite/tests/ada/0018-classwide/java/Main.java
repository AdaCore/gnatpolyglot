import com.adacore.libtest.Callbacks.Callback0;
import com.adacore.libtest.test.Child;
import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.TestPackage;

import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class Main {

    static class JavaChild extends Root {
        public JavaChild(int i) {
            super(i);
        }

        @Override
        public java.util.Optional<Root> fRoot(Root r2) {
            System.out.println("pRoot: " + (r2 instanceof Child));
            return java.util.Optional.of(new Root(r2));
        }

        @Override
        public void pRootA(Root r2) {
            System.out.println("pRootA: " + (r2 instanceof Child));
        }

        @Override
        public void pOutRootA(Root.Ref r2) {
            System.out.println("pOutRootA: " + (r2.get().get() instanceof Child));
        }

    }

    public static void main(String[] args) throws Throwable {
        assert TestPackage.getRoot().map(v -> !(v instanceof Child)).orElse(false);
        assert TestPackage.getChild().map(Child.class::isInstance).orElse(false);

        assert TestPackage.getRootA().map(v -> !(v instanceof Child)).orElse(false);
        assert TestPackage.getChildA().map(Child.class::isInstance).orElse(false);

        Root.Ref ptr = new Root.Ref(null);
        TestPackage.getRoot(ptr);
        assert ptr.get().isPresent();
        assert ptr.get().map(Object::getClass).map(Root.class::equals).orElse(false);
        TestPackage.getChild(ptr);
        assert ptr.get().map(Object::getClass).map(Child.class::equals).orElse(false);

        JavaChild c = new JavaChild(4);
        TestPackage.callFRoot(c, new Root(1));
        TestPackage.callFRoot(c, new Child(1, 2));

        Root tmpR = new Root(1);
        tmpR._setOwner(Owner.LIBRARY);
        TestPackage.callPRootA(c, tmpR);
        TestPackage.callPOutRootA(c, new Root.Ref(tmpR));
        Child tmpC = new Child(1, 2);
        tmpC._setOwner(Owner.LIBRARY);
        TestPackage.callPRootA(c, tmpC);
        TestPackage.callPOutRootA(c, new Root.Ref(tmpC));
        tmpR._setOwner(Owner.USER);
        tmpC._setOwner(Owner.USER);

        java.util.Optional<Root> cloned = TestPackage.clone(c);
        assert cloned.map(JavaChild.class::isInstance).orElse(false);
        cloned.ifPresent(r -> r.setI(5));
        System.out.println(
            c.getI() + " != " + cloned.map(Root::getI).get()
        );

        Root.Ref c2 = new Root.Ref(new JavaChild(5));
        c2.get().ifPresent(root -> root._setOwner(Owner.LIBRARY));
        TestPackage.free(c2);
        System.out.println(c2.get());

        Callback0 cb = (r) -> {
            System.out.format(
                "call_callback: %s, %s\n",
                r instanceof Child,
                r instanceof JavaChild
            );
        };
        TestPackage.callCallback(cb, new Root(1));
        TestPackage.callCallback(cb, new Child(1, 2));
        TestPackage.callCallback(cb, new JavaChild(2));
    }
}
