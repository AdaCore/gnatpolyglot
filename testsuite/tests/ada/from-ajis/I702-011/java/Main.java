import com.adacore.libtest.p.StructuredFileAccess;
import com.adacore.libtest.p.UpdateLock;

public class Main {
    public static void main(String[] args) {
        // Lock_Updates is bound as a primitive method of Structured_File_Access
        UpdateLock l = new StructuredFileAccess().lockUpdates();
        l.unlock();
    }
}
