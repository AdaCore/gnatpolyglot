import com.adacore.libtest.p.lists.Cursor;
import com.adacore.libtest.p.lists.List;
import com.adacore.libtest.p.lists.ListsPackage;

public class Main {

    public static void main(String[] args) {
        List l = new List();

        l.append(1, 1);
        l.append(2, 1);
        l.append(3, 1);
        l.append(4, 1);
        l.append(5, 1);

        Cursor c = l.first();

        while (!c.equals(ListsPackage.getNoElement())) {
            System.out.println(c.element());

            c = c.next();
        }
    }
}
