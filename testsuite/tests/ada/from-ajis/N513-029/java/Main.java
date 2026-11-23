import com.adacore.libtest.hello.GeneralEntityReference;

public class Main {
    public static void main(String[] args) {
        GeneralEntityReference ref = new GeneralEntityReference();
        ref.forEachDispatchingCall(null);
        System.out.println("OK");
    }
}
