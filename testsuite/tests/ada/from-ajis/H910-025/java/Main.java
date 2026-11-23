import com.adacore.gnatpolyglot.runtime.ada2java.PolyglotString;
import com.adacore.libtest.test.TrackNumberType;
import com.adacore.libtest.test.TrackType;

public class Main {

    public static void main(String[] argv) {
        // Track_Type / Track_Number_Type have no default constructor in the
        // generated API: build them with placeholder component values.
        TrackType t = new TrackType(0.0f, 0.0f, new TrackNumberType.Array(0, 25));

        TrackNumberType nullTN =
            new TrackNumberType(-1, new PolyglotString("               "));
        nullTN.setSourceType(0);
        PolyglotString invalidTN = sizeString("XXX", 15);
        nullTN.setTn(invalidTN);

        for (int i = t.getAllTns().getBegin(); i <= t.getAllTns().getEnd(); i++) {
            t.getAllTns().setUnslided(i, nullTN);
        }

        System.out.println("ELEM: " + t.getAllTns().getUnslided(1).getTn());
    }

    private static PolyglotString sizeString(String string, int size) {
        if (string.length() == size) {
            System.out.println("*** sizeString making SAME size "
                    + size + " /"
                    + string.length() + " /"
                    + string + "/");
            return new PolyglotString(string);
        } else if (string.length() > size) {
            String smaller = string.substring(0, size);
            System.out.println("*** sizeString making SMALLER size "
                    + size + " /"
                    + string.length() + " /"
                    + smaller.length() + " /"
                    + smaller + "/");
            return new PolyglotString(smaller);
        } else {
            int fillerCount = size - string.length();
            char[] fillerChar = new char[fillerCount];
            for (int i = 0; i < fillerChar.length; i++) {
                fillerChar[i] = ' ';
            }
            String filled = new String(string + new String(fillerChar));
            System.out.println("*** sizeString making BIGGER size "
                    + size + " /"
                    + string.length() + " /"
                    + filled.length() + " /"
                    + filled + "/");
            return new PolyglotString(filled);
        }
    }
}
