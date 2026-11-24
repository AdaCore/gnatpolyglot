import com.adacore.libtest.hello.GE;
import com.adacore.libtest.hello.RE;

public class Main {
    public static void main(String[] args) {
        try (RE e = new GE()) {
            if (e.pgi() == 1961) {
                System.out.println("OK");
            }
        }
    }
}
