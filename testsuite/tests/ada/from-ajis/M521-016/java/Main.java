import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        boolean failed = false;
        String exceptionName = "TEST.TEST_EXCEPTION";
        String exceptionMessage = "Test_Exception from Ada in Test_Symbolic_Traceback_From_Java";
        String location = "at test.adb:40";
        String information = TestPackage.testExceptionInformationFromJava().toString();
        if (!information.contains(exceptionName)) {
            System.out.println("exception information does'nt contain " + exceptionName);
            failed = true;
        }
        if (!information.contains(exceptionMessage)) {
            System.out.println("exception information does'nt contain " + exceptionMessage);
            failed = true;
        }
        String traceback = TestPackage.testSymbolicTracebackFromJava().toString();
        if (!traceback.contains(location)) {
            System.out.println("symbolic traceback does'nt contain " + location);
            failed = true;
        }
        if (failed) {
            // Retain the original diagnostic strings without dumping the environment.
            System.out.println(information);
            System.out.println(traceback);
        } else {
            System.out.print("OK");
        }
        System.out.flush();
    }
}
