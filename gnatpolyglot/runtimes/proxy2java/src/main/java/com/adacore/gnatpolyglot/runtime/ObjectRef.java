package com.adacore.gnatpolyglot.runtime;

import java.util.Optional;

public abstract class ObjectRef<T extends PolyglotObject> {

    protected T obj;

    public ObjectRef(T obj) {
        this.obj = obj;
    }

    public Optional<T> get() {
        return Optional.ofNullable(obj);
    }

    public void set(T obj) {
        this.obj = obj;
    }

    /**
     * Internal use only.
     *
     * <p> Return the data of the underlying object, or null if there is none.
     */
    public PolyglotData getData() {
        return get().map(PolyglotObject::_getData).orElse(null);
    }

    /**
     * Update the object held by the Ref. If the address of the data is different
     * from `addr`, create a new object of type T and set its internal address to
     * `addr`.
     */
    abstract protected void update(PolyglotData data);

}

