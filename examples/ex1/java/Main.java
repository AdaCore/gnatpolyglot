import com.adacore.libex1.example.Counter;

public class Main {
    public static void main(String[] args) {
        Counter c = new Counter();

        c.increment();
        System.out.println("count = " + c.getCount());
        c.increment(3);
        System.out.println("count = " + c.getCount());
        c.reset();
        System.out.println("count = " + c.getCount());
        c.setCount(5);
        System.out.println("count = " + c.getCount());
    }
}
