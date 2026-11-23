import com.adacore.libtest.test.TestPackage;

public class Main {

    public static boolean testEquals(Object v1, Object v2) {
        return v1.equals(v2);
    }

    public static void main(String[] args) {
        System.out.println(
            "EXPECTED EQUALS "
                + testEquals(TestPackage.getNewR((byte) 9), TestPackage.getNewR((byte) 9)));
        System.out.println(
            "EXPECTED NOT EQUALS "
                + testEquals(TestPackage.getNewR((byte) 9), TestPackage.getNewR((byte) 5)));
        System.out.println(
            "EXPECTED EQUALS "
                + testEquals(TestPackage.getNewRTagged((byte) 19), TestPackage.getNewRTagged((byte) 19)));
        System.out.println(
            "EXPECTED NOT EQUALS "
                + testEquals(TestPackage.getNewRTagged((byte) 19), TestPackage.getNewRTagged((byte) 15)));
        System.out.println(
            "EXPECTED NOT EQUALS, BUT WITHOUT EXCEPTION "
                + testEquals(TestPackage.getNewR((byte) 9), new String("yo")));
    }
}
