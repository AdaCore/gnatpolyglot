package com.adacore.gnatpolyglot.runtime.ada2java;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.gnatpolyglot.runtime.PolyglotObject;

public class IntegerArray extends PolyglotArray<Integer> {

    private static native ArrayData arrayAlloc(int first, int last);

    /**
     * Internal use only.
     *
     * <p> Create a ShortArray from an existing proxy data.
     */
    public IntegerArray(ArrayData data) {
        super(data);
    }

    /**
     * Internal use only.
     *
     * <p> Create an array from an existing proxy data and a parent object
     * that represents the root object that owns the memory of the array.
     */
    public IntegerArray(ArrayData data, PolyglotObject parent) {
        super(data, parent);
    }

    public IntegerArray(int first, int last) {
        super(arrayAlloc(first, last));
    }

    private static native void arrayFree(ArrayData data);

    @Override
    protected Consumer<PolyglotData> getFree() {
        return (arrayData) -> { arrayFree((ArrayData) arrayData); };
    }

    private static native int arrayGet(ArrayData data, int index);

    @Override
    public Integer getUnslided(int index) {
        return arrayGet(getData(), index);
    }

    private static native int arraySet(ArrayData data, int index, int element);

    @Override
    public Integer setUnslided(int index, Integer element) {
        Objects.requireNonNull(element);
        return arraySet(getData(), index, element);
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        return new IntegerArray(
            new ArrayData(getBegin(), getEnd() - 1, getData().addr, Owner.STATIC),
            this.parent
        );
    }

    private static native ArrayData arrayClone(ArrayData data);

    @Override
    public IntegerArray clone() {
        ArrayData data = arrayClone(getData());
        data.setOwner(Owner.USER);
        return new IntegerArray(data, this);
    }
}
