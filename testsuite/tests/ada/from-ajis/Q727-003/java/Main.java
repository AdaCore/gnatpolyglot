import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.libtest.hello.DeclaredEarly;
import com.adacore.libtest.hello.DeclaredEarlyNotTagged;
import com.adacore.libtest.hello.ReferenceType;
import com.adacore.libtest.hello.ReferenceTypeNotTagged;

public class Main {

    private static class Child extends DeclaredEarly {

        private String fText;

        private Child(String text) {
            super();
            fText = text;
        }

        @Override
        public String toString() {
            return fText;
        }
    }

    public static void main(String[] args) {
        DeclaredEarlyNotTagged dent = new DeclaredEarlyNotTagged(0);
        dent.setYear(1983);
        dent._setOwner(Owner.LIBRARY);
        ReferenceTypeNotTagged rtnt = new ReferenceTypeNotTagged(dent);
        if (rtnt.getRef().getYear() == 1983) {
            DeclaredEarly de = new Child("OK");
            de._setOwner(Owner.LIBRARY);
            ReferenceType rt = new ReferenceType(de);
            System.out.println(rt.getRef());
        }
    }
}
