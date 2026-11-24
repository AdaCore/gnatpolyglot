import com.adacore.libtest.test.ARecord;

public class Main {

    public static void main(String[] args) {
        ARecord r = new ARecord(10);

        r.initialize();

        System.out.println("RESULT = " + r.getSum());
    }
}
