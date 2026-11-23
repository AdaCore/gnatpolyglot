import com.adacore.libtest.t.Rec;

public class Main {
    public static void main(String[] args) {
        Rec r = new Rec(1234, 0, 0, 0);
        System.out.println("The discriminant of Rec r is " + r.getDisc());
    }
}
