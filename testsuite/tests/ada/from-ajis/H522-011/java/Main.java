import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        TestPackage.getProcedure().accept(10);
        TestPackage.callAProcedure(11, TestPackage.getProcedure());
    }
}
