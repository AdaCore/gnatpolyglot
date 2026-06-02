import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Root;
import com.adacore.libtest.test.Child;

import java.lang.reflect.Modifier;

import com.adacore.libtest.test.Child;

public class Main {

    public static class JavaChild extends Root {

        public JavaChild() {
        }

        @Override
        public void p2(int i) {
            p1();

            System.out.println("JavaChild.p2() " + i);
        }

    }

    public static void main(String[] args) throws Throwable {
        assert Modifier.isAbstract(Root.class.getModifiers());

        Child child = new Child(3);

        child.p1();
        child.p2(1);

        JavaChild javaChild = new JavaChild();

        javaChild.p1();
        javaChild.p2(2);

        TestPackage.p2Root(javaChild, 4);
    }

}
