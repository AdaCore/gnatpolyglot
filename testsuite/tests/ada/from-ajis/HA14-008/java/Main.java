import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.Contained;
import com.adacore.libtest.test.Container;
import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {
        Container c = new Container(new Contained());

        Root r = new Root() {
            @Override
            public void prim(Container obj) {
                System.out.println("PRIM CALLED FROM JAVA");
                System.out.flush();
            }

            @Override
            public void prim2(PolyglotString str) {
                System.out.println("PRIM2 CALLED FROM JAVA: " + str.toString());
                System.out.flush();
            }
        };

        TestPackage.callPrim(r, c);
        TestPackage.callPrim2(r);

        c = null;
        r = null;

        System.gc();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

}
