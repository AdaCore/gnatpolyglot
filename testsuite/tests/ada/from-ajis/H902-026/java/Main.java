import com.adacore.libtest.test.Bla;

public class Main {

    public static class Child extends Bla {
        public Child(int v) {
            super(v);
        }
    }

    public static void main(String[] args) {
        Bla x = new Bla(5);
        Bla y = new Child(5);
        System.out.println("Object creation worked");
    }
}
