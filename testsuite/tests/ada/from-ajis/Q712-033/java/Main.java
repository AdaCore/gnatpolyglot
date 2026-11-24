import com.adacore.libtest.hello.HelloPackage;
import com.adacore.libtest.hello.T;

public class Main {

    public static void main(String[] args) {
        T t1 = HelloPackage.f1();
        t1 = HelloPackage.f2();
        t1 = HelloPackage.fa1().get();
        t1 = HelloPackage.fa2().get();
        T.Ref ref1 = new T.Ref(t1);
        HelloPackage.p1(t1, ref1, ref1, t1, ref1, ref1);
        HelloPackage.p2(t1, ref1, ref1, t1, ref1, ref1);
        System.out.println("OK");
    }
}
