import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.ada.calendar.Time;
import com.adacore.libtest.ada.strings.unbounded.UnboundedString;

public class Main {

    public static void main(String[] args) throws Throwable {
        Time time = TestPackage.getTime();
        UnboundedString s = new UnboundedString();
        TestPackage.append(s, time);
        System.out.println(TestPackage.toString(s).toString());

        Time.Array arr = new Time.Array(1, 3);
        for (int i = 1; i <= 3; i++)
            arr.setUnslided(i, time);
        TestPackage.appendArr(s, arr);
        System.out.println(TestPackage.toString(s).toString());
    }
}
