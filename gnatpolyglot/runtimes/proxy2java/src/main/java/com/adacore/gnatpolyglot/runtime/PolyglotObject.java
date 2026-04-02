package com.adacore.gnatpolyglot.runtime;

import java.lang.ref.Cleaner;
import java.util.function.Consumer;
import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

/**
 * Base class of all GNATpolyglot objects.
 *
 * <p>When the data of a PolyglotObject is set, the object also gets registered in a
 * {@link Cleaner}. This allows the Garbage Collector to automatically clean the allocated data when
 * the object is no longer referenced.
 *
 * <p>The registration of the object in a {@link Cleaner} should only be considered as a safety net.
 * It does not guarantee an order of deallocation, nor that the object may be freed if it is still
 * referenced until the end of the program.
 *
 * <p>In order to remedy these issues, the class also implements {@link AutoCloseable}. When the
 * {@link AutoCloseable::close} method is called, the hook given to the Garbage Collector is also
 * called, freeing the allocated native object. When overriding the {@link AutoCloseable::close}
 * method, please ensure to call the implementation of PolyglotObject.
 */
public abstract class PolyglotObject implements AutoCloseable {
    /** Global Cleaner for all bound libraries. */
    private static Cleaner CLEANER = Cleaner.create();

    /** Runnable object to free a pointer. */
    private static class CleaningAction implements Runnable {

        /** The data to free. */
        private PolyglotData data;

        /** The function to free the data. */
        private Consumer<PolyglotData> free;

        CleaningAction(PolyglotData data, Consumer<PolyglotData> free) {
            this.data = data;
            this.free = free;
        }

        @Override
        final public void run() {
            if (this.data != null && this.data.getOwner() == Owner.USER) {
                free.accept(this.data);
                this.data = null;
            }
        }
    }

    /** The native object. */
    protected PolyglotData data;

    /** Handle to clean up the allocated native object */
    private Cleaner.Cleanable cleanable;

    protected PolyglotObject(PolyglotData data) {
        setData(data);
    }

    /** Return the native object. */
    public final PolyglotData getData() {
        return data;
    }

    /** Sets the native data of the PolyglotObject and register the data to the global Cleaner. */
    protected final void setData(PolyglotData data) {
        this.data = data;
        if (data != null)
            this.cleanable = CLEANER.register(this, new CleaningAction(data, getFree()));
    }

    public final Owner getOwner() {
        return data.getOwner();
    }

    public final void setOwner(Owner owner) {
        data.setOwner(owner);
    }

    @Override
    public final void close() {
        cleanable.clean();
        data = null;
    }

    /** Return the function to free the heap memory. */
    abstract protected Consumer<PolyglotData> getFree();
}
