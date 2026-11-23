import com.adacore.libtest.p1.Rec1;

public class Main {

    public static void main(String[] args) {
        Rec1 v = new Rec1();
        v.getM().set(1, 5, 99);

        System.out.println(v.getM().get(1, 5));
    }
}
