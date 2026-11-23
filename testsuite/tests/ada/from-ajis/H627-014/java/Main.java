import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.libtest.test.AType;
import java.util.function.Consumer;

public class Main {
    static class JavaChild extends AType {
        @Override
        protected Consumer<PolyglotData> _getFree() {
            Consumer<PolyglotData> freeNative = super._getFree();
            // close() is final and runs the Cleaner action. Extend that action
            // instead of overriding Java finalize(). Do not capture this.
            return data -> {
                freeNative.accept(data);
                System.out.println("FINALIZE FROM JAVA");
            };
        }
    }

    public static void main(String[] args) {
        // Explicit close replaces nondeterministic GC-driven finalization.
        System.out.println("Closing AType...");
        try (AType simple = new AType()) {
        }
        System.out.println("AType closed.");

        System.out.println("Closing JavaChild...");
        // Close through the generated base, retaining the subclass cleanup.
        try (AType simple = new JavaChild()) {
        }
        System.out.println("JavaChild closed.");
    }
}
