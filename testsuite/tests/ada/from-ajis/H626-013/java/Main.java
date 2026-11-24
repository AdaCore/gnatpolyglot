import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        if (!TestPackage.createString(true).isPresent()) {
            System.out.println("STRING GOT IS NULL");
        } else {
            System.out.println("STRING GOT IS NOT NULL");
        }

        System.out.println("PRINTING A NULL STRING ACCESS AS A STRING ACCESS");
        TestPackage.printStringAccess(null);

        //  This is not supposed to work, since we don't expect a pointer
        //  here...
        //  System.out.println ("PRINTING A NULL STRING ACCESS AS A STRING");
        //  TestPackage.printString(null);
    }
}
