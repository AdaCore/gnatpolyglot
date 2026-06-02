import com.adacore.libtest.test.Child;
import com.adacore.libtest.test.OtherChild;
import com.adacore.libtest.test.priv.PrivRoot;
import com.adacore.libtest.test.publ.Publ;
import com.adacore.libtest.test.Rec;
import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.TestPackage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Root root = new Root(1, 2);
        Rec recr = new Rec(1, 2);

        root.p1();

        Child child = new Child(3, 4, 5);
        Rec recc = new Rec(3, 4);

        child.p1();

        root.p2(recr);
        child.p2(recc);

        OtherChild otherChild = new OtherChild(3, 4, 5);
        otherChild.p1();

        Root foo = otherChild;
        foo.p1();

        TestPackage.pRoot(root);
        TestPackage.pRoot(child);
        TestPackage.pRoot(otherChild);

        PrivRoot priv = new PrivRoot();
        priv.foo();

        Publ publ = new Publ(1, 3);
        publ.foo();
    }
}
