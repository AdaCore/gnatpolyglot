import com.adacore.libtest.Callbacks;
import com.adacore.libtest.test.TestPackage;

public class Main {

    public static void main(String[] argv) {

        System.out.println(TestPackage.callChar(new Callbacks.Callback0() {

            @Override
            public Character apply(Character v) {
                return v;
            }}, 'c'));

        System.out.println(TestPackage.callLong(new Callbacks.Callback1() {

            @Override
            public Long apply(Long v) {
                return v;
            }}, 99));
    }

}
