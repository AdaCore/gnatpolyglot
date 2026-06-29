import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.adacore.libtest.test.TestPackage;
import com.adacore.libtest.test.Exc;
import com.adacore.libtest.test.T;

public class Main {

    static AtomicInteger counter = new AtomicInteger();

    static void f(int n) {
        try {
            TestPackage.getException(n);
        } catch (Exc e) {
            counter.addAndGet(Integer.valueOf(e.getMessage().strip()));
        }
        TestPackage.unregister();
    }

    static class Inherit extends T { }

    public static void main(String[] args) throws Throwable {
        var futures = new ArrayList<CompletableFuture<?>>(100);
        int reference = 0;
        for (int i = 0; i < 100; i++) {
            int copy = i;
            reference += i;
            futures.add(CompletableFuture.runAsync(() -> f(copy))); 
        }
        futures.stream().forEach(CompletableFuture::join);
        System.out.println(counter.get() + " == " + reference);

        futures.clear();

        Inherit inherit = new Inherit();
        for (int i = 0; i < 100; i++) {
            final int j = i;
            futures.add(
                CompletableFuture.runAsync(() -> {
                    TestPackage.doCopy(inherit, j);
                    TestPackage.unregister();
                })
            );
            // Test that simultaneous classwide copies do not break.
            // In java, there is object-wide state for polymorphic copies called
            // from ada. Verify that data is still correctly set.
            assert ((Inherit)inherit.clone())._getData() != null;
        }
        futures.stream().forEach(CompletableFuture::join);
    }
}
