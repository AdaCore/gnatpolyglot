import com.adacore.libtest.hello.HelloPackage;

public class Main {

    public static void main(String[] args) {
        HelloPackage.setConst(5);
        HelloPackage.setBreak(6);
        HelloPackage.p(HelloPackage.getConst() + HelloPackage.getBreak());
    }
}
