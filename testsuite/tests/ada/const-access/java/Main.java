import com.adacore.libtest.test.TestPackage;

import java.util.Optional;

import com.adacore.libtest.test.T;

public class Main {

    public static void main(String[] args) throws Throwable {
        Optional<T> t = TestPackage.id(null);
    }
}
