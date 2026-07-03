import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.inst.InstPackage;
import com.adacore.libtest.test.top_level_child.TopLevelChildPackage;
import com.adacore.libtest.top_level_inst.TopLevelInstPackage;

public class Main {

    public static void main(String[] args) throws Throwable {
        com.adacore.libtest.test.inst.T obj = new com.adacore.libtest.test.inst.T(42);
        InstPackage.p();
        obj.prim();
        com.adacore.libtest.test.inst.nested.NestedPackage.n();

        System.out.println();
        com.adacore.libtest.top_level_inst.T topLevel = new com.adacore.libtest.top_level_inst.T(43);
        TopLevelInstPackage.p();
        topLevel.prim();
        com.adacore.libtest.top_level_inst.nested.NestedPackage.n();

        System.out.println();
        com.adacore.libtest.test.top_level_child.T childInst = new com.adacore.libtest.test.top_level_child.T(44);
        TopLevelChildPackage.p();
        childInst.prim();
        com.adacore.libtest.test.top_level_child.nested.NestedPackage.n();

        System.out.println();
        System.out.println(com.adacore.libtest.test.subgen.SubgenPackage.f(21, 22).getI());
        System.out.println(com.adacore.libtest.test.transitive1.subgen.SubgenPackage.f(21, 22).getI());
        System.out.println(com.adacore.libtest.test.transitive2.subgen.SubgenPackage.f(21, 22).getI());
        System.out.println(com.adacore.libtest.transitive3.subgen.SubgenPackage.f(21, 22).getI());
        System.out.println(com.adacore.libtest.test.transitive4.subgen.SubgenPackage.f(21, 22).getI());
    }
}
