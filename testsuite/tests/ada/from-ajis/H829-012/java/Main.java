import com.adacore.libtest.test.Rec;

public class Main {
    public static void main(String[] args) {
        Rec v = new Rec();

        v.p1();

        // AJIS: Test_Package.P_2 (v) -- P_2 was annotated "Attached => False"
        // in AJIS; GNATpolyglot always binds it as a method of Rec.
        v.p2();
    }
}
