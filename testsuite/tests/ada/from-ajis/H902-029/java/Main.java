import com.adacore.libtest.test.Bla;
import com.adacore.libtest.test.SomeRecord;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static class Child extends Bla {
        @Override
        public void prim(int param1, SomeRecord param2) {
            System.out.println("GOT FROM JAVA : " + param1);
        }
    }

    public static void main(String[] args) {
        Bla x = new Bla();
        Bla y = new Child();
        SomeRecord rec = new SomeRecord(10);
        TestPackage.callPrim(x, 1, rec);
        TestPackage.callPrim(y, 2, rec);
    }
}
