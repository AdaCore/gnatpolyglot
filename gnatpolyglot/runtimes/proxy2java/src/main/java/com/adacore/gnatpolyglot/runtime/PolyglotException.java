package com.adacore.gnatpolyglot.runtime;

import java.lang.ref.Cleaner;
import java.util.function.Consumer;

import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

public abstract class PolyglotException extends RuntimeException implements AutoCloseable {

    /** The native exception. */
    protected PolyglotData data;

    /** Handle to clean up the allocated native object */
    private Cleaner.Cleanable cleanable;

    protected PolyglotException(PolyglotData data, String message) {
        super(message);
        setData(data);
    }

    protected PolyglotException(PolyglotData data) {
        setData(data);
    }

    /** Return the native object. */
    public PolyglotData getData() {
        return data;
    }

    /** Sets the native data of the PolyglotObject and register the data to the global Cleaner. */
    protected final void setData(PolyglotData data) {
        this.data = data;
        if (data != null)
            this.cleanable = PolyglotCleaner.register(this, data, getFree());
    }

    public final Owner getOwner() {
        return data.getOwner();
    }

    public final void setOwner(Owner owner) {
        data.setOwner(owner);
    }

    public PolyglotData release() {
        PolyglotData res = data;
        // Prevent the data from being freed: there is no way to cancel a corresponding Cleanable
        // from running once an object has been registered
        res.setOwner(Owner.STATIC);
        // The object should not be usable anymore.
        setData(null);
        return res;
    }

    @Override
    public final void close() {
        cleanable.clean();
        data = null;
    }

    /** Return the function to free the heap memory. */
    abstract protected Consumer<PolyglotData> getFree();
}
