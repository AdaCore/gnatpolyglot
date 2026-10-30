import com.adacore.libtest.sample.Known;
import com.adacore.libtest.sample.KnownChild;
import com.adacore.libtest.sample.Forward;
import com.adacore.libtest.sample.Defaulted;
import com.adacore.libtest.sample.Plain;

public class Main {
    static class GuestKnown extends Known {
        GuestKnown(int n) { super(n); }
    }
    static class GuestChild extends KnownChild {
        GuestChild(int n) { super(n); }
    }
    static class GuestForward extends Forward {
        GuestForward(int n) { super(n); }
    }
    static class GuestDefaulted extends Defaulted {}
    static class GuestPlain extends Plain {}

    public static void main(String[] args) {
        var known = new Known(3);
        var child = new KnownChild(4);
        var forward = new Forward(8);
        var guest = new GuestKnown(5);
        var guestChild = new GuestChild(6);
        var guestForward = new GuestForward(7);
        var plain = new GuestPlain();
        try (var defaulted = new GuestDefaulted()) {
            System.out.println("known: " + known.getN() + " " + child.getN()
                               + " " + forward.getN());
            System.out.println("shadows: " + guest.getN() + " " + guestChild.getN()
                               + " " + guestForward.getN() + " " + defaulted.getN());
        }
    }
}
