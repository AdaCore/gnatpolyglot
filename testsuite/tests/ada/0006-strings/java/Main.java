import com.adacore.libtest.test.TestPackage;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        PolyglotString adaArr = TestPackage.stringFunc();
        TestPackage.stringProc(adaArr);
        String adaStr = adaArr.toString();
        System.out.format("Ada.String from Java: %s\n", adaStr);

        PolyglotString userArr = TestPackage.userStringFunc();
        TestPackage.userStringProc(userArr);
        String userStr = userArr.toString();
        System.out.format("Test.User_Str from Java: %s\n", userStr);

        TestPackage.userStringProc(adaArr);
        TestPackage.stringProc(userArr);

        String javaStr = "from Java";
        PolyglotString javaArr = new PolyglotString(javaStr);
        TestPackage.userStringProc(javaArr);
        TestPackage.stringProc(javaArr);
    }
}
