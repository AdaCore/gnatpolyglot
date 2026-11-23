import com.adacore.libtest.test.T3;

public class Main {

    private static class Child extends T3 {
        //  T_1.Val has no default value: no parameterless constructor is
        //  generated.
        Child(int val) {
            super(val);
        }
    }

    public static void main(String[] args) {
        T3 v = new Child(1);

        v.setVal(999);

        //  AJIS renamed "F return Int_1" to F_Int_1 via pragma Annotate;
        //  GNATpolyglot binds that overload as f().
        System.out.println(v.f());
    }
}
