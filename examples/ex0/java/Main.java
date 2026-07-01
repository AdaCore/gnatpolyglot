import com.adacore.libex0.example.ExamplePackage;

import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;

public class Main {
    public static void main(String[] args) {
        ExamplePackage.hello();
        ExamplePackage.hello(new PolyglotString("Java"));
    }
}
