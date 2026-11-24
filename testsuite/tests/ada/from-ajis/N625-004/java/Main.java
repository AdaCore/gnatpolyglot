import com.adacore.gnatpolyglot.runtime.BooleanRef;
import com.adacore.libtest.hello.FileInfo;
import com.adacore.libtest.hello.file_info_sets.Cursor;
import com.adacore.libtest.hello.file_info_sets.Set;

public class Main {
    public static void main(String[] args) {
        try (Set set = new Set();
             FileInfo item = new FileInfo();
             FileInfo equivalent = new FileInfo();
             Cursor position = new Cursor()) {
            assert set.isEmpty() && set.length() == 0;
            try (Cursor first = set.first()) {
                assert !first.hasElement();
            }

            BooleanRef inserted = new BooleanRef(false);
            set.insert(item, position, inserted);
            assert inserted.getValue();
            assert !set.isEmpty() && set.length() == 1;
            assert position.hasElement();

            // Null-record values are equivalent: this must not add a second node.
            set.insert(equivalent, position, inserted);
            assert !inserted.getValue() && set.length() == 1;
            assert set.contains(equivalent);
            try (Cursor found = set.find(equivalent);
                 FileInfo element = found.element().orElseThrow()) {
                assert found.hasElement();
                assert element.getClass() == FileInfo.class;
            }
            try (Cursor first = set.first(); Cursor end = first.next()) {
                assert first.hasElement();
                assert !end.hasElement();
            }

            try (Set copy = new Set(set)) {
                assert copy.length() == 1 && copy.contains(item);
                copy.deleteFirst();
                assert copy.isEmpty() && copy.length() == 0;
                assert set.length() == 1;
            }
            set.delete(equivalent);
            assert set.isEmpty() && set.length() == 0;
            set.insert(item);
            set.clear();
            assert set.isEmpty() && set.length() == 0;
        }
        System.out.println("OK");
    }
}
