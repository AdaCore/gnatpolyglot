package com.adacore.gnatpolyglot.runtime;

import java.util.function.*;

public class Functions {

    public static class CallbackData {
        public long addr;
        public long data;
        public long extra;

        public CallbackData(long addr, long data, long extra) {
            this.addr = addr;
            this.data = data;
            this.extra = extra;
        }
    }

    private static abstract class FunctionRef<F> {
        F f;

        public void set(F f) {
            this.f = f;
        }

        public F get() {
            return f;
        }
    }

    @FunctionalInterface
    public interface Function0<R> extends Supplier<R> {
        class Ref<R> extends FunctionRef<Function0<R>> {}
    }

    @FunctionalInterface
    public interface Function1<A, R> extends Function<A, R> {
        class Ref<A, R> extends FunctionRef<Function1<A, R>> {}
    }

    @FunctionalInterface
    public interface Function2<A, B, R> extends BiFunction<A, B, R> {
        class Ref<A, B, R> extends FunctionRef<Function2<A, B, R>> {}
    }

    @FunctionalInterface
    public interface Function3<A, B, C, R> {
        R apply(A a, B b, C c);

        class Ref<A, B, C, R> extends FunctionRef<Function3<A, B, C, R>> {}
    }

    @FunctionalInterface
    public interface Function4<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);

        class Ref<A, B, C, D, R> extends FunctionRef<Function4<A, B, C, D, R>> {}
    }

    @FunctionalInterface
    public interface Function5<A, B, C, D, E, R> {
        R apply(A a, B b, C c, D d, E e);

        class Ref<A, B, C, D, E, R> extends FunctionRef<Function5<A, B, C, D, E, R>> {}
    }

    @FunctionalInterface
    public interface Function6<A, B, C, D, E, F, R> {
        R apply(A a, B b, C c, D d, E e, F f);

        class Ref<A, B, C, D, E, F, R> extends FunctionRef<Function6<A, B, C, D, E, F, R>> {}
    }

    @FunctionalInterface
    public interface Function7<A, B, C, D, E, F, G, R> {
        R apply(A a, B b, C c, D d, E e, F f, G g);

        class Ref<A, B, C, D, E, F, G, R> extends FunctionRef<Function7<A, B, C, D, E, F, G, R>> {}
    }

    @FunctionalInterface
    public interface Function8<A, B, C, D, E, F, G, H, R> {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h);

        class Ref<A, B, C, D, E, F, G, H, R> extends FunctionRef<Function8<A, B, C, D, E, F, G, H, R>> {}
    }

    @FunctionalInterface
    public interface Function9<A, B, C, D, E, F, G, H, I, R> {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i);

        class Ref<A, B, C, D, E, F, G, H, I, R> extends FunctionRef<Function9<A, B, C, D, E, F, G, H, I, R>> {}
    }

    @FunctionalInterface
    public interface Function10<A, B, C, D, E, F, G, H, I, J, R>  {
        R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);

        class Ref<A, B, C, D, E, F, G, H, I, J, R> extends FunctionRef<Function10<A, B, C, D, E, F, G, H, I, J, R>> {}
    }

    @FunctionalInterface
    public interface Consumer0 extends Runnable {

	class Ref extends FunctionRef<Consumer0> {}
    }

    @FunctionalInterface
    public interface Consumer1<A> extends Consumer<A> {
	class Ref<A> extends FunctionRef<Consumer1<A>> {}
    }

    @FunctionalInterface
    public interface Consumer2<A, B> extends BiConsumer<A, B> {
	class Ref<A, B> extends FunctionRef<Consumer2<A, B>> {}
    }

    @FunctionalInterface
    public interface Consumer3<A, B, C> {
        void accept(A a, B b, C c);

        class Ref<A, B, C> extends FunctionRef<Consumer3<A, B, C>> {}
    }

    @FunctionalInterface
    public interface Consumer4<A, B, C, D> {
        void accept(A a, B b, C c, D d);

        class Ref<A, B, C, D> extends FunctionRef<Consumer4<A, B, C, D>> {}
    }

    @FunctionalInterface
    public interface Consumer5<A, B, C, D, E> {
        void accept(A a, B b, C c, D d, E e);

        class Ref<A, B, C, D, E> extends FunctionRef<Consumer5<A, B, C, D, E>> {}
    }

    @FunctionalInterface
    public interface Consumer6<A, B, C, D, E, F> {
        void accept(A a, B b, C c, D d, E e, F f);

        class Ref<A, B, C, D, E, F> extends FunctionRef<Consumer6<A, B, C, D, E, F>> {}
    }

    @FunctionalInterface
    public interface Consumer7<A, B, C, D, E, F, G> {
        void accept(A a, B b, C c, D d, E e, F f, G g);

        class Ref<A, B, C, D, E, F, G> extends FunctionRef<Consumer7<A, B, C, D, E, F, G>> {}
    }

    @FunctionalInterface
    public interface Consumer8<A, B, C, D, E, F, G, H> {
        void accept(A a, B b, C c, D d, E e, F f, G g, H h);

        class Ref<A, B, C, D, E, F, G, H> extends FunctionRef<Consumer8<A, B, C, D, E, F, G, H>> {}
    }

    @FunctionalInterface
    public interface Consumer9<A, B, C, D, E, F, G, H, I> {
        void accept(A a, B b, C c, D d, E e, F f, G g, H h, I i);

        class Ref<A, B, C, D, E, F, G, H, I> extends FunctionRef<Consumer9<A, B, C, D, E, F, G, H, I>> {}
    }

    @FunctionalInterface
    public interface Consumer10<A, B, C, D, E, F, G, H, I, J>  {
        void accept(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);

        class Ref<A, B, C, D, E, F, G, H, I, J> extends FunctionRef<Consumer10<A, B, C, D, E, F, G, H, I, J>> {}
    }
}
