import com.adacore.libtest.parent.Rec;
import com.adacore.libtest.test.TestPackage;

public class Main {
    public static void main(String[] args) {
        TestPackage.setX(new Rec());
        System.out.println("CREATION OF REC WORKED");
    }
}
