import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        var v = TestPackage.getNullPAcc();
        if (v == null) {
            System.out.println("V is null");
        }
        TestPackage.callPAcc(null);
        TestPackage.callPAcc(() -> System.out.println("P_Acc from Java..."));
    }
}
