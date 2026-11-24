import com.adacore.libtest.p.PPackage;
import com.adacore.libtest.p.ProjectTreeRef;

public class Main {
    public static void main(String[] args) {
        ProjectTreeRef tree = new ProjectTreeRef();
        System.out.println(PPackage.valueOf(0, 0, tree));
    }
}
