import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] args) throws Throwable {
        System.out.println("Global_Int = " + TestPackage.getGlobalInt());
        TestPackage.setGlobalInt(TestPackage.getGlobalInt() + 1);
        System.out.println("Global_Int = " + TestPackage.getGlobalInt());

        System.out.println("Global_Int = " + TestPackage.getGlobalRec().getI());
        TestPackage.incrementRec(false, TestPackage.getGlobalRec());
        System.out.println("Global_Int = " + TestPackage.getGlobalRec().getI());

        System.out.print("Global_Arr = {");
        for (var e : TestPackage.getGlobalArr()) {
            System.out.print(e + ", ");
        }
        System.out.println("}");

        TestPackage.incrementArr(TestPackage.getGlobalArr());

        System.out.print("Global_Arr = {");
        for (var e : TestPackage.getGlobalArr()) {
            System.out.print(e + ", ");
        }
        System.out.println("}");

        TestPackage.setGlobalB(TestPackage.getGlobalB() + 1);
        System.out.println("Global_A = " + TestPackage.getGlobalA());
        System.out.println("Global_B = " + TestPackage.getGlobalB());

        System.out.println("Withed_Type = " + TestPackage.getWithedType().getI());
    }
} 
