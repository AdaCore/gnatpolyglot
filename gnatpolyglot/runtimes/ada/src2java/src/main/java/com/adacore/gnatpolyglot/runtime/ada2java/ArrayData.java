package com.adacore.gnatpolyglot.runtime.ada2java;

import com.adacore.gnatpolyglot.runtime.PolyglotData;

public class ArrayData extends PolyglotData {

    public int begin;
    public int end;
    public long addr;

    public ArrayData(int begin, int end, long addr, Owner owner) {
        super(owner);
        this.begin = begin;
        this.end = end;
        this.addr = addr;
    }

    /**
     * Internal use only.
     *
     * The goal of this constructor is to simplify the creation of ArrayData from
     * JNI. Once returned, Java can set the correct owner.
     */
    public ArrayData(int begin, int end, long addr) {
        super(Owner.UNKNOWN);
        this.begin = begin;
        this.end = end;
        this.addr = addr;
    }

    @Override
    public long getAddress() {
        return addr;
    }
}
