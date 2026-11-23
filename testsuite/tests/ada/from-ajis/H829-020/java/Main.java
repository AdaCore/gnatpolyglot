import java.util.Optional;

import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.AType;
import com.adacore.libtest.test.TestPackage;

public class Main {

    static class Child extends AType {
        @Override
        public Optional<PolyglotString> returnString() {
            //  This used to be called several times due to poor lock checking
            //  mechanism

            System.out.println("CALLED FROM JAVA");

            PolyglotString str = new PolyglotString("YO FROM JAVA");
            str._setOwner(Owner.LIBRARY);

            return Optional.of(str);
        }
    }

    public static void main(String[] args) {
        System.out.println(TestPackage.callbackReturnString(new AType()).get());
        System.out.println(TestPackage.callbackReturnString(new Child()).get());
    }
}
