//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime;

import java.lang.ref.Cleaner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.BiFunction;
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

    /** The native object. */
    protected PolyglotData data;

    /**
     * The root native object owning the memory.
     *
     * <p>This should be equal to `this` by default. Getters to a field set this value to the
     * parent object whose getter was called in order to avoid the root object from being
     * collected while some of its component are still accessible.
     */
    protected final PolyglotObject parent;

    /** Handle to clean up the allocated native object */
    private Cleaner.Cleanable cleanable;

    /**
     * Internal flag to indicate whether the {@link PolyglotObject#clone} function
     * should call a native clone function.
     */
    private boolean internalCloning = false;

    protected PolyglotObject(PolyglotData data) {
        _setData(data);
        this.parent = this;
    }

    protected PolyglotObject(PolyglotData data, PolyglotObject parent) {
        _setData(data);
        this.parent = parent;
    }

    /** Return the native object. */
    public PolyglotData _getData() {
        return data;
    }

    /** Sets the native data of the PolyglotObject and register the data to the global Cleaner. */
    protected final void _setData(PolyglotData data) {
        this.data = data;
        if (data != null)
            this.cleanable = PolyglotCleaner.register(this, data, _getFree());
    }

    public final Owner _getOwner() {
        return data.getOwner();
    }

    public final void _setOwner(Owner owner) {
        data.setOwner(owner);
    }

    public PolyglotData _release() {
        PolyglotData res = data;
        // Prevent the data from being freed: there is no way to cancel a corresponding Cleanable
        // from running once an object has been registered
        res.setOwner(Owner.STATIC);
        // The object should not be usable anymore.
        _setData(null);
        return res;
    }

    /**
     * Create a clone of the object, and set the address of the object to the given one.
     *
     * <p>This function should only be called from JNI.
     */
    @SuppressWarnings("unused")
    synchronized private Object _internalClone(long addr) throws CloneNotSupportedException  {
        // Flag the copy as "internal". This indicates to the `clone` funtion
        // that it should not call a native clone function.
        this.internalCloning = true;
        PolyglotObject c = (PolyglotObject) clone();
        this.internalCloning = false;
        c.internalCloning = false;
        // Set the data of the new object.
        c._setData(new PolyglotData.Pointer(addr, Owner.LIBRARY));
        return c;
    }

    @Override
    synchronized protected Object clone() throws CloneNotSupportedException {
        // Begin by creating a shallow copy of the object
        PolyglotObject c = (PolyglotObject) super.clone();
        // If the call to `clone` originates from a call to `internalClone`, then
        // the new data already exists and will be set by the former at the end
        // of the current call.
        if (!internalCloning) {
            c._setData(
                    new com.adacore.gnatpolyglot.runtime.PolyglotData.Pointer(
                            c._isShadow()
                                    ? c._getCloneShadow().apply(this._getData(), c)
                                    : c._getClone().apply(this._getData()),
                            com.adacore.gnatpolyglot.runtime.PolyglotData.Owner.USER));
        }
        return c;
    }

    protected boolean _isShadow() {
        return false;
    }

    /** Return the function to free the heap memory. */
    protected Function<PolyglotData, Long> _getClone() {
        return null;
    }

    /** Return the function to free the heap memory. */
    protected BiFunction<PolyglotData, PolyglotObject, Long> _getCloneShadow() {
        return null;
    }


    @Override
    public final void close() {
        cleanable.clean();
        data = null;
    }

    /** Return the function to free the heap memory. */
    abstract protected Consumer<PolyglotData> _getFree();
}
