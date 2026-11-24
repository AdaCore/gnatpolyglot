import com.adacore.libtest.test.ATaggedRecord;

public class Main {

    static class Child extends ATaggedRecord {
        public Child(int i) {
            super(i);
        }
    }

    public static void main(String[] args) {
        ATaggedRecord t1, t2;
        t1 = new ATaggedRecord(30);
        t2 = new Child(40);
        System.out.println(t1.getI() + ", " + t2.getI());
    }
}
