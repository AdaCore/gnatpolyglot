import com.adacore.libtest.test.ARecord;

public class Main {
    public static void main(String[] args) {
        ARecord first, second;

        // No default constructor: use the aggregate-like one with
        // default-ish values.
        first = new ARecord(null, 0);
        second = new ARecord(null, 0);

        second.setV(20);

        first.setNext(second);

        System.out.println("Second value is: " + second.getV());
    }
}
