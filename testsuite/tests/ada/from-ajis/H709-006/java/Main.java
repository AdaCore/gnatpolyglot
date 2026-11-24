import com.adacore.libtest.test.ATaggedRecord;

public class Main {

    private static class Child extends ATaggedRecord {
        @Override
        public void initialize() {
            //  super.initialize();

            System.out.println("CHILD INITIALIZE");
        }
    }

    public static void main(String[] args) {
        ATaggedRecord rec = new ATaggedRecord();
        ATaggedRecord rec2 = new Child();
    }
}
