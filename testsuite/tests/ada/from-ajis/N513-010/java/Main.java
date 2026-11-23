import com.adacore.libtest.hello.GeneralXrefDatabaseRecord;
import com.adacore.libtest.hello.lang_specific_databases_maps.Map;

public class Main {
    public static void main(String[] args) {
        // The generated constructor requires the initial Map component.
        try (Map map = new Map();
             GeneralXrefDatabaseRecord db = new GeneralXrefDatabaseRecord(map)) {
            System.out.println("OK");
        }
    }
}
