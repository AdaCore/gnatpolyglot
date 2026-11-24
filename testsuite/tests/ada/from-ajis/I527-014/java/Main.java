import com.adacore.libtest.p.PPackage;

public class Main {
    public static void main(String[] args) {
        PPackage.getV2().setUnslided(1, 987);
        System.out.println(PPackage.getV2().getUnslided(1));
    }
}
