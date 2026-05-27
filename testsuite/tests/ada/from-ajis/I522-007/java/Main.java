
import com.adacore.libtest.p.nested.NestedPackage;

public class Main {

    public static void main(String[] argv) {
        NestedPackage.setV(987);
        System.out.println(NestedPackage.getV());
    }
}
