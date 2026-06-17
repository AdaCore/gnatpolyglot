package com.adacore.gnatpolyglot.runtime.ada2java;

import com.adacore.gnatpolyglot.runtime.PolyglotObject;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

/**
 * Base class of all arrays return from native Ada functions.
 *
 * <p>Attemps at modifying the size of the array will throw an UnsupportedOperationException.
 *
 * <p>The index of the first element of the array is obtained by calling
 * {@link PolyglotArray#getBegin}. The value may be non-zero for some array. The index of the
 * last element in obtained by calling {@link PolyglotArray#getEnd}.
 *
 * <p>Calling {@link PolyglotArray#get} or {@link PolyglotArray#set} automatically slides indexes
 * to 0. In order to use Ada indexing, the corresponding {@link PolyglotArray#getUnslided} and
 * {@link PolyglotArray#setUnslided} must be used.
 */
public abstract class PolyglotArray<E> extends PolyglotObject implements List<E>, RandomAccess {

    public PolyglotArray(ArrayData data) {
        super(data);
    }

    protected PolyglotArray(ArrayData data, PolyglotObject parent) {
        super(data, parent);
    }

    public int getBegin() {
        return getData().begin;
    }

    public int getEnd() {
        return getData().end;
    }

    public abstract E getUnslided(int index);

    @Override
    public E get(int index) {
        return getUnslided(index + getBegin());
    }

    public abstract E setUnslided(int index, E element);

    @Override
    public E set(int index, E element) {
        return setUnslided(index + getBegin(), element);
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

    @Override
    public final boolean add(E e) {
        throw new UnsupportedOperationException("List is not mutable");
    }

    @Override
    public int size() {
        ArrayData data = getData();
        return data.end - data.begin + 1;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return stream().filter(e -> Objects.equals(o, e)).findAny().isPresent();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public E next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return get(index++);
            }

        };
    }

    @Override
    public Object[] toArray() {
        return this.stream().toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size) {
            a = (T[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size);
        }

        int index = 0;
        for (E e : this) {
            a[index++] = (T) e;
        }

        if (a.length > size)
            a[size] = null;

        return a;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("List is not mutable");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("List is not mutable");
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("List is not mutable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("List is not mutable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("List is not mutable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("List is not mutable");
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("List is not mutable");
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("List is not mutable");
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size(); i++) {
            if (Objects.equals(o, get(i))) return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int i = size() - 1;
        for (; i >= 0; i--) {
            if (Objects.equals(o, get(i))) break;
        }
        return i;
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListIterator<E>() {

            int i = index;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return get(i++);
            }

            @Override
            public boolean hasPrevious() {
                return i >= 0;
            }

            @Override
            public E previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return get(i--);
            }

            @Override
            public int nextIndex() {
                return i + 1;
            }

            @Override
            public int previousIndex() {
                return i - 1;
            }

            @Override
            public void remove() {
                PolyglotArray.this.remove(i);
            }

            @Override
            public void set(E e) {
                PolyglotArray.this.set(i, e);
            }

            @Override
            public void add(E e) {
                PolyglotArray.this.add(e);
            }
        };
    }

    static {
        Library.loadLibrary();
    }

}
