package com.adacore.gnatpolyglot.runtime.ada2java;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.adacore.gnatpolyglot.runtime.ObjectRef;
import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.gnatpolyglot.runtime.PolyglotObject;

public class BooleanArray extends PolyglotArray<Boolean> {

    private static native ArrayData arrayAlloc(int first, int last);

    /**
     * Internal use only.
     *
     * <p> Create a ShortArray from an existing proxy data.
     */
    public BooleanArray(ArrayData data) {
        super(data);
    }

    /**
     * Internal use only.
     *
     * <p> Create an array from an existing proxy data and a parent object
     * that represents the root object that owns the memory of the array.
     */
    public BooleanArray(ArrayData data, PolyglotObject parent) {
        super(data, parent);
    }

    public BooleanArray(int first, int last) {
        super(arrayAlloc(first, last));
    }

    private static native void arrayFree(ArrayData data);

    @Override
    protected Consumer<PolyglotData> getFree() {
        return (arrayData) -> { arrayFree((ArrayData) arrayData); };
    }

    private static native boolean arrayGet(ArrayData data, int index);

    @Override
    public Boolean getUnslided(int index) {
        return arrayGet(getData(), index);
    }

    private static native boolean arraySet(ArrayData data, int index, boolean element);

    @Override
    public Boolean setUnslided(int index, Boolean element) {
        Objects.requireNonNull(element);
        return arraySet(getData(), index, element);
    }

    @Override
    public List<Boolean> subList(int fromIndex, int toIndex) {
        return new BooleanArray(
            new ArrayData(getBegin(), getEnd() - 1, getData().addr, Owner.STATIC),
            this.parent
        );
    }

    private static native ArrayData arrayClone(ArrayData data);

    @Override
    public BooleanArray clone() {
        ArrayData data = arrayClone(getData());
        data.setOwner(Owner.USER);
        return new BooleanArray(data, this);
    }

    public static final class Ref extends ObjectRef<BooleanArray> {
        public Ref(BooleanArray obj) {
            super(obj);
        }

        private Ref(ArrayData data) {
            super(data == null ? null : new BooleanArray(data));
            if (data != null) data.setOwner(Owner.LIBRARY);
        }

        @Override
        protected void update(PolyglotData data) {
            PolyglotData oldData = get().map(o -> o.getData()).orElse(null);
            if (!Objects.deepEquals(data, oldData)) {
                if (data == null) {
                    this.obj = null;
                } else {
                    this.obj = new BooleanArray(
                            (com.adacore.gnatpolyglot.runtime.ada2java.ArrayData) data);
                }
            }
        }
    }
}

