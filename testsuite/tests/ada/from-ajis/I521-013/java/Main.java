import com.adacore.libtest.lib_inst.LibInstPackage;
import com.adacore.libtest.p.MyRec;
import com.adacore.libtest.p.PPackage;

public class Main {

    public static void main(String[] args) {
        com.adacore.libtest.p.inst_1.R r1 = new com.adacore.libtest.p.inst_1.R(0);
        r1.store(99);
        System.out.println(r1.get());

        com.adacore.libtest.p.inst_2.R r2 =
            new com.adacore.libtest.p.inst_2.R(new MyRec(0));
        MyRec rec = new MyRec(0);
        rec.setVal(77);
        r2.store(rec);
        System.out.println(r2.get().getVal());

        r2 = PPackage.create();
        rec = new MyRec(0);
        rec.setVal(66);
        r2.store(rec);
        System.out.println(r2.get().getVal());

        PPackage.instProc(rec);

        com.adacore.libtest.lib_inst.R r3 = new com.adacore.libtest.lib_inst.R(0);
        r3.store(42);
        System.out.println(r3.get());

        com.adacore.libtest.p.inst_1.gen_inst.GenInstPackage.setX(43);
        System.out.println(com.adacore.libtest.p.inst_1.gen_inst.GenInstPackage.getX());
    }
}
