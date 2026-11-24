import com.adacore.libtest.hello.AdaRecord;

// PC16-003: Ada_Record is a tagged type. Hello.Test takes 12 parameters
// covering all combinations of by-value, class-wide, access, in-out, and out
// modes for Ada_Record and Ada_Record'Class.
//
// Ada_Record_Class_Ptr (access to classwide type) is not yet bindable, so
// Hello.Test is not generated. Additionally, the Java printer does not support
// tagged types. This test is XFAIL.

public class Main {
    public static void main(String[] argv) {
        AdaRecord v1 = new AdaRecord(1);
        v1.print();
    }
}
