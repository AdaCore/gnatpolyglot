import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.hello.ChildType;
import com.adacore.libtest.hello.HelloPackage;
import com.adacore.libtest.hello.ParentType;

public class Main {

    public static class JavaParent extends ParentType {

        @Override
        public void proc(ParentType.Ref object) {
            say();
            object.get().get().say();
        }

        @Override
        public void say() {
            System.out.println("Java Parent");
        }

    }

    public static class JavaChild extends ChildType {

        // As in the AJIS test, this overrides the Child_Type_Access overload
        // of Proc (``Ref`` resolves to ChildType.Ref here), not the inherited
        // Parent_Type_Access one.
        @Override
        public void proc(ChildType.Ref object) {
            say();
            object.get().get().say();
        }

        @Override
        public void say() {
            System.out.println("Java Child");
        }

    }

    public static void run(ParentType p, ParentType p1, ParentType p2) {
        ParentType.Ref ref = new ParentType.Ref(null);
        ref.set(p);
        p.proc(ref);
        ref.set(p1);
        p.proc(ref);
        ref.set(p2);
        p.proc(ref);
    }

    public static void run(ChildType p, ChildType p1, ChildType p2) {
        ParentType.Ref ref = new ParentType.Ref(null);
        ref.set(p);
        p.proc(ref);
        ref.set(p1);
        p.proc(ref);
        ref.set(p2);
        p.proc(ref);
    }

    public static void main(String[] args) {
        ParentType ap = HelloPackage.getAdaParent();
        ChildType ac = HelloPackage.getAdaChild();
        ParentType p = new ParentType();
        p._setOwner(Owner.LIBRARY);
        ChildType c = new ChildType();
        c._setOwner(Owner.LIBRARY);
        JavaParent jp = new JavaParent();
        jp._setOwner(Owner.LIBRARY);
        JavaChild jc = new JavaChild();
        jc._setOwner(Owner.LIBRARY);
        run(ap, p, jp);
        run(p, jp, ap);
        run(jp, ap, p);
        run(ac, c, jc);
        run(c, jc, ac);
        run(jc, ac, c);
    }

}
