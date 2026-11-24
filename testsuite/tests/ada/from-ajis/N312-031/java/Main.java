import com.adacore.libtest.hello.HelloPackage;

public class Main {

    public static void main(String[] args) {
        if (HelloPackage.getOClass().getI() == 1961) {
            System.out.println("OK");
        }
    }
}
