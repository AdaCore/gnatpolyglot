import com.adacore.libtest.p.i.IPackage;

public class Main {

    public static void main(String[] args) {
        IPackage.add(1, 2, v3 -> System.out.println("TOTAL = " + v3));

        IPackage.add2(10, 20, v3 -> System.out.println("TOTAL = " + v3));
    }
}
