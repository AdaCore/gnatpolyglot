import com.adacore.libex4.example.ExamplePackage;
import com.adacore.libex4.example.P;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public class Main {

    static void escapeAcc() {
        P acc = new P();
        acc._setOwner(Owner.LIBRARY);
        ExamplePackage.setAcc(acc);
        ExamplePackage.print();
    }

    public static void main(String[] args) {
        escapeAcc();
        ExamplePackage.print();
        P acc = ExamplePackage.getAcc().orElse(null);
        P.Ref ref = new P.Ref(acc);
        ExamplePackage.uncheckedFree(ref);
        acc = ref.get().orElse(null);

        ExamplePackage.setAcc(null);
        ExamplePackage.print();

        escapeAcc();
        ExamplePackage.print();
        acc = ExamplePackage.getAcc().orElse(null);
        acc._setOwner(Owner.USER);
        ExamplePackage.setAcc(null);
    }
}
