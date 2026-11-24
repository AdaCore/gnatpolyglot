import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.ARecord;

public class Main {
    public static void main(String[] args) {
        ARecord obj = new ARecord(999, 118.0f, new PolyglotString("Kisses from Java"));
        System.out.println(obj.getA() + ", " + obj.getB() + ", " + obj.getC().get());
    }
}
