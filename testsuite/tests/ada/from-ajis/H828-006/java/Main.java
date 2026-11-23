import com.adacore.libtest.test.RootType;
import com.adacore.libtest.test.SomeType;

public class Main {

    static class Inner extends RootType {
        @Override
        public SomeType primitive() {
            throw new RuntimeException("cannot implement this...");
        }
    }

    public static void main(String[] args) {
        RootType v = new Inner();

        try {
            v.primitive();
        } catch (Exception e) {
            System.out.println("CORRECT PRIMITIVE CALLED");
        }
    }
}
