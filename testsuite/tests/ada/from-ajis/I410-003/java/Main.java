import com.adacore.libtest.ref.RefPackage;
import com.adacore.libtest.vfs.VirtualFile;

public class Main {

    public static void main(String[] argv) {

        // Java arrays are 0-based: index 0 is Ada index 1
        RefPackage.getX().set(0, new VirtualFile());

        RefPackage.getX().get(0).prim(RefPackage.getX());

        System.out.println("NO EXCEPTION");

    }
}
