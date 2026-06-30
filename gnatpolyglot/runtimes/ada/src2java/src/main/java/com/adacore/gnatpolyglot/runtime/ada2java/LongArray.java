//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

package com.adacore.gnatpolyglot.runtime.ada2java;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.adacore.gnatpolyglot.runtime.ObjectRef;
import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;
import com.adacore.gnatpolyglot.runtime.PolyglotObject;

public class LongArray extends PolyglotArray<Long> {

    private static native ArrayData arrayAlloc(int first, int last);

    /**
     * Internal use only.
     *
     * <p> Create a ShortArray from an existing proxy data.
     */
    public LongArray(ArrayData data) {
        super(data);
    }

    /**
     * Internal use only.
     *
     * <p> Create an array from an existing proxy data and a parent object
     * that represents the root object that owns the memory of the array.
     */
    public LongArray(ArrayData data, PolyglotObject parent) {
        super(data, parent);
    }

    public LongArray(int first, int last) {
        super(arrayAlloc(first, last));
    }

    private static native void arrayFree(ArrayData data);

    @Override
    protected Consumer<PolyglotData> _getFree() {
        return (arrayData) -> { arrayFree((ArrayData) arrayData); };
    }

    private static native long arrayGet(ArrayData data, int index);

    @Override
    public Long getUnslided(int index) {
        return arrayGet(_getData(), index);
    }

    private static native long arraySet(ArrayData data, int index, long element);

    @Override
    public Long setUnslided(int index, Long element) {
        Objects.requireNonNull(element);
        return arraySet(_getData(), index, element);
    }

    @Override
    public List<Long> subList(int fromIndex, int toIndex) {
        return new LongArray(
            new ArrayData(getBegin(), getEnd() - 1, _getData().addr, Owner.STATIC),
            this.parent
        );
    }

    private static native ArrayData arrayClone(ArrayData data);

    @Override
    public LongArray clone() {
        ArrayData data = arrayClone(_getData());
        data.setOwner(Owner.USER);
        return new LongArray(data, this);
    }

    public static final class Ref extends ObjectRef<LongArray> {
        public Ref(LongArray obj) {
            super(obj);
        }

        private Ref(ArrayData data) {
            super(data == null ? null : new LongArray(data));
            if (data != null) data.setOwner(Owner.LIBRARY);
        }

        @Override
        protected void update(PolyglotData data) {
            PolyglotData oldData = get().map(o -> o._getData()).orElse(null);
            if (!Objects.deepEquals(data, oldData)) {
                if (data == null) {
                    this.obj = null;
                } else {
                    this.obj = new LongArray(
                            (com.adacore.gnatpolyglot.runtime.ada2java.ArrayData) data);
                }
            }
        }
    }
}
