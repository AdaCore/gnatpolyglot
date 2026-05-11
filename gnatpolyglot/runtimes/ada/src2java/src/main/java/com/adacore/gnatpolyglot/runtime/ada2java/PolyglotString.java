package com.adacore.gnatpolyglot.runtime.ada2java;

import java.util.function.Consumer;

import com.adacore.gnatpolyglot.runtime.PolyglotData;
import com.adacore.gnatpolyglot.runtime.PolyglotObject;
import com.adacore.gnatpolyglot.runtime.PolyglotData.Owner;

/**
 * Class to represent a String returned from native Ada functions.
 *
 * <p>Conversions to {@link String} are performed using {@link PolyglotString#toString}. The operation
 * creates a copy of the character sequence, and doing the operation repetitively is not recommended.
 *
 * <p>The index of the first element of the array is obtained by calling
 * {@link PolyglotString#getBegin}. The value may be non-zero for some array. The index of the
 * last element in obtained by calling {@link PolyglotString#getEnd}.
 *
 * <p>Calling {@link PolyglotString#charAt} or {@link PolyglotString#setCharAt} automatically slides indexes
 * to 0. In order to use Ada indexing, the corresponding {@link PolyglotString#charAtUnslided} and
 * {@link PolyglotString#setCharAtUnslided} must be used.
 */
public class PolyglotString extends PolyglotObject implements CharSequence {

    public PolyglotString(ArrayData data) {
        super(data);
    }

    public PolyglotString(ArrayData data, PolyglotObject parent) {
        super(data, parent);
    }

    static private native ArrayData fromString(String str);

    public PolyglotString(String str) {
        super(null);
        ArrayData data = fromString(str);
        data.setOwner(Owner.USER);
        setData(data);
    }

    public int getBegin() {
        return getData().begin;
    }

    public int getEnd() {
        return getData().end;
    }

    private static native char stringGet(ArrayData data, int index);

    @Override
    public char charAt(int index) {
        return charAtUnslided(index + getBegin());
    }

    public char charAtUnslided(int index) {
        return stringGet(getData(), index);
    }

    private static native void stringSet(ArrayData data, int index, char c);

    public void setCharAt(int index, char value) {
        setCharAtUnslided(index + getBegin(), value);
    }

    public void setCharAtUnslided(int index, char value) {
        stringSet(getData(), index, value);
    }

    @Override
    public int length() {
        ArrayData data = getData();
        return data.end - data.begin + 1;
    }

    /** Return the native object. */
    @Override
    public final ArrayData getData() {
        return (ArrayData) data;
    }

    @Override
    public ArrayData release() {
        return (ArrayData) super.release();
    }

    private static native void stringFree(ArrayData data);

    @Override
    protected Consumer<PolyglotData> getFree() {
        return (data) -> { stringFree((ArrayData) data); };
    }

    static private native String toJavaString(ArrayData str);

    @Override
    public String toString() {
        return toJavaString(getData());
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new PolyglotString((String) this.toString().subSequence(start, end));
    }

}
