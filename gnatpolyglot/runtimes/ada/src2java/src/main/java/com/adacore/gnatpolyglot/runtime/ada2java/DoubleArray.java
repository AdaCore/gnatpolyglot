package com.adacore.gnatpolyglot.runtime.ada2java;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.adacore.gnatpolyglot.runtime.ObjectRef;
import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.gnatpolyglot.runtime.PolyglotObject;

public class DoubleArray extends PolyglotArray<Double> {

    private static native ArrayData arrayAlloc(int first, int last);

    /**
     * Internal use only.
     *
     * <p> Create a ShortArray from an existing proxy data.
     */
    public DoubleArray(ArrayData data) {
        super(data);
    }

    /**
     * Internal use only.
     *
     * <p> Create an array from an existing proxy data and a parent object
     * that represents the root object that owns the memory of the array.
     */
    public DoubleArray(ArrayData data, PolyglotObject parent) {
        super(data, parent);
    }

    public DoubleArray(int first, int last) {
        super(arrayAlloc(first, last));
    }

    private static native ArrayData arrayFree(ArrayData data);

    @Override
    protected Consumer<PolyglotData> getFree() {
        return (arrayData) -> { arrayFree((ArrayData) arrayData); };
    }

    private static native double arrayGet(ArrayData data, int index);

    @Override
    public Double getUnslided(int index) {
        return arrayGet(getData(), index);
    }

    private static native double arraySet(ArrayData data, int index, double element);

    @Override
    public Double setUnslided(int index, Double element) {
        Objects.requireNonNull(element);
        return arraySet(getData(), index, element);
    }

    @Override
    public List<Double> subList(int fromIndex, int toIndex) {
        return new DoubleArray(
            new ArrayData(getBegin(), getEnd() - 1, getData().addr, Owner.STATIC),
            this.parent
        );
    }

    private static native ArrayData arrayClone(ArrayData data);

    @Override
    public DoubleArray clone() {
        ArrayData data = arrayClone(getData());
        data.setOwner(Owner.USER);
        return new DoubleArray(data);
    }

    public static final class Ref extends ObjectRef<DoubleArray> {
        public Ref(DoubleArray obj) {
            super(obj);
        }

        private Ref(ArrayData data) {
            super(data == null ? null : new DoubleArray(data));
            if (data != null) data.setOwner(Owner.LIBRARY);
        }

        @Override
        protected void update(PolyglotData data) {
            PolyglotData oldData = get().map(o -> o.getData()).orElse(null);
            if (!Objects.deepEquals(data, oldData)) {
                if (data == null) {
                    this.obj = null;
                } else {
                    this.obj = new DoubleArray(
                            (com.adacore.gnatpolyglot.runtime.ada2java.ArrayData) data);
                }
            }
        }
    }
}
