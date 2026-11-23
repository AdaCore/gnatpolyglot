import com.adacore.libtest.test.T;

public class Main {

    public static void main(String[] args) {
        //  T has no default value for A, so no parameterless constructor is
        //  generated: build it with an arbitrary value, then set A.
        T v = new T(0);

        v.setA(999);

        v.p();
    }
}
