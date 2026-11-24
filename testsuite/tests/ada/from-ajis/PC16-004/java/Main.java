import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.hello.RecordType;
import com.adacore.libtest.hello.TaggedRecordType;
import com.adacore.libtest.hello.HelloPackage;

// PC16-004: Plain record + tagged record with String-returning Msg function
// and class-wide Say procedure, including dispatch to a Java override.

public class Main {
    public static void main(String[] args) {
        // Plain record: set I = 1 + 1994 = 1995, call Say
        RecordType r = new RecordType();
        r.setI(r.getI() + 1994);
        r.say();

        // Tagged record: set I = 1 + 2004 = 2005, call class-wide Say
        TaggedRecordType tr = new TaggedRecordType();
        tr.setI(tr.getI() + 2004);
        HelloPackage.say(tr);

        // Java subclass overriding Msg to return " 2012"
        TaggedRecordType jm = new TaggedRecordType() {
            @Override
            public PolyglotString msg() {
                return new PolyglotString(" 2012");
            }
        };
        HelloPackage.say(jm);
    }
}
