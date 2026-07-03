import com.adacore.libgnatcoll_core.gnatcoll.strings.Xstring;
import com.adacore.libgnatcoll_core.gnatcoll.strings.StringsPackage;

import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.gnatpolyglot.runtime.IntegerRef;

public class Main {

    static String JSHORT;
    static String JLONG;

    static PolyglotString SHORT;
    static PolyglotString LONG;

    static void testAppend() {
        Xstring s = new Xstring();
        Xstring s2 = new Xstring();
        s.set(SHORT);
        assert s.operatorEq(SHORT);

        s.set(LONG);

        s2.copyFrom(s);
        s.append(SHORT);
        assert s2.operatorEq(LONG);
        assert s.operatorEq(new PolyglotString(JLONG + JSHORT));

        s.set(SHORT);
        assert s.operatorEq(SHORT);
        assert s2.operatorEq(LONG);

        s.set(LONG);
        s2.copyFrom(s);
        s.slice(2, 3);
        s.append(SHORT);
        assert s2.operatorEq(LONG);
        assert s.operatorEq(new PolyglotString(LONG.subSequence(1, 3) + JSHORT));
    }

    public static void main(String[] args) throws Throwable {
        JSHORT = "ABCD";
        SHORT = new PolyglotString(JSHORT);
        JLONG = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        LONG = new PolyglotString(JLONG);
        testAppend();
    }
}
