import com.adacore.libtest.test.Enum;
import com.adacore.libtest.test.Rec;
import com.adacore.libtest.test.SmallRec;
import com.adacore.libtest.test.TestPackage;

public class Main {
    private static String enumValue(Enum value) {
        // An invalid value returned by a packed field accessor maps to null.
        return value == null ? "null" : Byte.toString(value.value);
    }

    public static void main(String[] args) {
        try (SmallRec d = new SmallRec(Enum.E1, Enum.E1);
             SmallRec e = new SmallRec(Enum.E3, Enum.E1);
             Rec r = new Rec(false, false, Enum.E1, d, e)) {
            System.out.println("r.D.F1 = " + enumValue(r.getD().getF1()));
            System.out.println("r.E.F1 = " + enumValue(r.getE().get().getF1()));
        }

        try (SmallRec.Array arr = new SmallRec.Array(1, 3)) {
            arr.getUnslided(1).setF2(Enum.E1);
            arr.getUnslided(2).setF2(Enum.E2);
            arr.getUnslided(3).setF2(Enum.E3);
            for (int i = 1; i <= 3; ++i) {
                System.out.println("arr [" + i + "] = " + enumValue(arr.getUnslided(i).getF2()));
            }
        }

        TestPackage.getGlobalVar().setF1(Enum.E1);
        TestPackage.getGlobalVar().setF2(Enum.E2);
        System.out.println("Global_Var.F1 = " + enumValue(TestPackage.getGlobalVar().getF1()));
        System.out.println("Global_Var.F2 = " + enumValue(TestPackage.getGlobalVar().getF2()));
    }
}
