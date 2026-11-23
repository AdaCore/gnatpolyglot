import com.adacore.libtest.test.TestPackage;

public class Main {
    private static class MyException extends RuntimeException {
    }

    public static void main(String[] args) {
        MyException original = new MyException();
        try {
            TestPackage.callCallback(() -> { throw original; });
            System.out.println("NO EXCEPTION");
        } catch (MyException e) {
            System.out.println("EXCEPTION EQUALS: " + (e == original));
        } catch (RuntimeException e) {
            System.out.println("UNEXPECTED EXCEPTION: " + e.getMessage());
        }
    }
}
