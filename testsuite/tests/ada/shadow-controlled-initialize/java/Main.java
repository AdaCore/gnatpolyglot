import com.adacore.libtest.pkg.Priv;
import com.adacore.libtest.pkg.Pub;
import com.adacore.libtest.pkg.PubValue;
import com.adacore.libtest.pkg.child.PubInherit;
import com.adacore.libtest.pkg.child.PubOverride;

public class Main {

    static class PubChild extends Pub {}

    static class PubValueChild extends PubValue {
        PubValueChild() {
            super();
        }

        PubValueChild(int v) {
            super(v);
        }
    }

    static class PrivChild extends Priv {}

    static class PubInheritChild extends PubInherit {}

    static class PubOverrideChild extends PubOverride {}

    public static void main(String[] args) {
        // Initialize must be called exactly once for each object
        System.out.println("Pub:");
        Pub pub = new Pub();
        System.out.println("PubChild:");
        Pub pubChild = new PubChild();
        System.out.println("PubValue():");
        PubValue pubValue = new PubValue();
        System.out.println("PubValue(1):");
        PubValue pubValue1 = new PubValue(1);
        System.out.println("PubValueChild():");
        PubValue pubValueChild = new PubValueChild();
        System.out.println("PubValueChild(1):");
        PubValue pubValueChild1 = new PubValueChild(1);
        System.out.println("Priv:");
        Priv priv = new Priv();
        System.out.println("PrivChild:");
        Priv privChild = new PrivChild();
        System.out.println("PubInherit:");
        PubInherit pubInherit = new PubInherit();
        System.out.println("PubInheritChild:");
        PubInherit pubInheritChild = new PubInheritChild();
        System.out.println("PubOverride:");
        PubOverride pubOverride = new PubOverride();
        System.out.println("PubOverrideChild:");
        PubOverride pubOverrideChild = new PubOverrideChild();
    }
}
