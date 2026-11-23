import com.adacore.libtest.hello.HelloPackage;

public class Main {
    public static void main(String[] args) {
        var t = HelloPackage.getV();
        String result = t.getUnslided(t.getBegin()).toString();
        System.out.println(result);
    }
}
