package com.mengcraft.cleaner.util;

import java.util.Iterator;

public class ArrayVector<E> implements Iterator<E> {

    private final E[] array;
    private int cursor;
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayVector) {
            return toString().equals(obj.toString());
        }
        return false;
    }

    @Override
    public boolean hasNext() {
        return array.length != cursor;
    }

    @Override
    public E next() {
        return hasNext() ? array[cursor++] : null;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(array[i]);
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    public boolean contains(E element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) { return true; }
        }
        return false;
    }

    public E get(int index) {
        if (index < 0 || index >= array.length) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }

    public E get() {
        return hasNext() ? array[cursor] : null;
    }

    public int remain() {
        return array.length - cursor;
    }

    public int cursor() {
        return cursor;
    }

    public E[] array() {
        return array;
    }

    @SuppressWarnings("unchecked")
    public ArrayVector(E... array) {
        if (array == null) {
            throw new NullPointerException();
        }
        this.array = array;
    }

}
