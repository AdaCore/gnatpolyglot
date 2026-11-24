import com.adacore.libtest.iterable_containers.IntegerContainer;

public class Main {
    public static void main(String[] args) {
        // Preserve the original enhanced-for check: Iterable is not mapped yet.
        for (Integer i : new IntegerContainer().iterate()) {
            System.out.println(i + " Integer_Container");
        }
    }
}
