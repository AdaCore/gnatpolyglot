import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.T;

import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

public class Main {

    public static void main(String[] args) throws Throwable {
        T t = new T(new PolyglotString("aaaaaaaaaa"));
        System.out.println(t.getS().toString());
        t.setS(new PolyglotString("bbbbbbbbbb"));
        t.print();
    }
}
