import com.adacore.libtest.hello.HelloRecord;

public class Main {
    public static void main(String[] args) {
        // B has no default value in Ada, so no parameterless constructor is
        // generated: pass an arbitrary value (Say does not read it).
        System.out.println(new HelloRecord(false).say());
    }
}
