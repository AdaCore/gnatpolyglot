import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

import com.adacore.gnatpolyglot.runtime.ada2java.IntegerArray;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

import com.adacore.libtest.test.TestPackage;

import java.util.Optional;

import com.adacore.libtest.test.Rec;
import com.adacore.libtest.test.Root;

public class Main {

    static class Child extends Root {

        @Override
        public Optional<Rec> rootRec(Rec acc) {
            System.out.println("Java got: " + acc.getI());
            return Optional.of(new Rec(40));
        }

        @Override
        public void rootRecP(com.adacore.libtest.test.Rec.Ref acc) {
            System.out.println("Java got: " + acc.get().get().getI());
            acc.set(new Rec(100));
        }

        @Override
        public Optional<IntegerArray> rootArr(IntegerArray acc) {
            System.out.print("Java got: {");
            for (var e : acc) {
                System.out.print(e + ", ");
            }
            System.out.println("}");
            IntegerArray ptr = new IntegerArray(10, 20);
            for (int i = ptr.getBegin(); i <= ptr.getEnd(); i++) {
                ptr.setUnslided(i, i);
            }
            ptr._setOwner(Owner.LIBRARY);
            return Optional.of(ptr);
        }

        @Override
        public void rootArrP(IntegerArray.Ref acc) {
            System.out.print("Java got: {");
            for (var e : acc.get().get()) {
                System.out.print(e + ", ");
            }
            System.out.println("}");
            IntegerArray ptr = new IntegerArray(10, 20);
            for (int i = ptr.getBegin(); i <= ptr.getEnd(); i++) {
                ptr.setUnslided(i, i);
            }
            ptr._setOwner(Owner.LIBRARY);
            acc.set(ptr);
        }

        @Override
        public Optional<PolyglotString> rootStr(PolyglotString acc) {
            System.out.println("Java got: " + acc.toString());
            PolyglotString res = new PolyglotString("From Java");
            res._setOwner(Owner.LIBRARY);
            return Optional.of(res);
        }

        @Override
        public void rootStrP(PolyglotString.Ref acc) {
            System.out.println("Java got: " + acc.get().map(Object::toString).orElseThrow());
            PolyglotString res = new PolyglotString("From Java");
            res._setOwner(Owner.LIBRARY);
            acc.set(res);
        }

    }

    static void dynamicDispatch() {
        Child c = new Child();
        Rec r = new Rec(42);
        r._setOwner(Owner.LIBRARY);
        Rec rec = TestPackage.callRootRec(c, r).get();
        System.out.println(rec.getI());
        rec._setOwner(Owner.USER);

        IntegerArray a = new IntegerArray(1, 10);
        for (int i = a.getBegin(); i <= a.getEnd(); i++) {
            a.setUnslided(i, i);
        }
        a._setOwner(Owner.LIBRARY);
        IntegerArray arr = TestPackage.callRootArr(c, a).get();
        System.out.format("%d .. %d = {", arr.getBegin(), arr.getEnd());
        for (var e : arr) {
            System.out.print(e + ", ");
        }
        System.out.println("}");
        a._setOwner(Owner.USER);
        arr._setOwner(Owner.USER);
    }

    static void inOut() {
        Child c = new Child();
        Rec.Ref r = new Rec.Ref(new Rec(42));
        r.get().ifPresent(rec -> rec._setOwner(Owner.LIBRARY));
        TestPackage.callRootRecP(c, r);
        System.out.println(r.get().get().getI());

        IntegerArray a = new IntegerArray(1, 10);
        for (int i = a.getBegin(); i <= a.getEnd(); i++) {
            a.setUnslided(i, i);
        }
        a._setOwner(Owner.LIBRARY);
        IntegerArray.Ref arrPtr = new IntegerArray.Ref(a);
        TestPackage.callRootArrP(c, arrPtr);
        IntegerArray arr = arrPtr.get().get();
        System.out.format("%d .. %d = {", arr.getBegin(), arr.getEnd());
        for (var e : arr) {
            System.out.print(e + ", ");
        }
        System.out.println("}");
        arr._setOwner(Owner.USER);
    }

    static void string() {
        Child c = new Child();
        PolyglotString ptr1 = new PolyglotString("begin");
        ptr1._setOwner(Owner.LIBRARY);
        PolyglotString ptr2 = TestPackage.callRootStr(c, ptr1).get();
        System.out.println("Got from Ada: " + ptr2.toString());
        ptr2._setOwner(Owner.USER);

        PolyglotString.Ref ref = new PolyglotString.Ref(ptr1);
        TestPackage.callRootStrP(c, ref);
        System.out.println("Got from Ada: " + ref.get().get().toString());
        ref.get().ifPresent(s -> s._setOwner(Owner.USER));

        ptr1._setOwner(Owner.USER);
    }

    // Default implementations already return null.
    static class Nulls extends Root {}

    static void nulls() {
        Nulls n = new Nulls();
        assert TestPackage.callRootRec(n, null).isEmpty();
        assert TestPackage.callRootArr(n, null).isEmpty();
        assert TestPackage.callRootStr(n, null).isEmpty();
        Rec.Ref rRef = new Rec.Ref(null);
        IntegerArray.Ref aRef = new IntegerArray.Ref(null);
        PolyglotString.Ref sRef = new PolyglotString.Ref(null);
        TestPackage.callRootRecP(n, rRef);
        TestPackage.callRootArrP(n, aRef);
        TestPackage.callRootStrP(n, sRef);
        assert rRef.get().isEmpty();
        assert aRef.get().isEmpty();
        assert sRef.get().isEmpty();
    }

    public static void main(String[] args) throws Throwable {
        dynamicDispatch();
        System.out.println();
        inOut();
        System.out.println();
        string();
        System.out.println();
        nulls();
    }
}
