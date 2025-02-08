package io.github.some_example_name;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;

public class GapBuffer<T> extends AbstractList<T> {

    private static final int DEFAULT_SIZE = 1024;

    private Object[] elementData;

    private int size;

    private int gapStart;

    private int gapEnd;

    private int cursor;

    public GapBuffer() {
        this(DEFAULT_SIZE);
    }

    public GapBuffer(int capacity) {
        elementData = new Object[capacity];
        gapStart = 0;
        gapEnd = elementData.length - 1;
    }

    public int cursorMoveRight(){
        if(cursor == size) {
            return cursor;
        }
        cursor++;
        return cursor;
    }

    public int cursorMoveLeft(){
        if(cursor == 0) {
            return cursor;
        }
        cursor--;
        return cursor;
    }

    public int getCursor() {
        return cursor;
    }

    public int moveCursor(int index){
        if(0<index && index < size) {
            cursor = index;
            return cursor;
        }
        return -1;
    }

    private int move(int index) {
        if (index < gapStart) {
            int count = gapStart - index;
            System.arraycopy(elementData, index, elementData, gapEnd - count + 1, count);
            gapStart = index;
            gapEnd = gapEnd - count;
            // It is okay to not set null for these elements, but for GC perspective, it
            // is recommended to remove these invalid elements.
            Arrays.fill(elementData, gapStart, gapEnd, null);
        } else if (index > gapStart) {
            int count = index - gapStart;
            System.arraycopy(elementData, gapEnd + 1, elementData, gapStart, count);
            gapStart = index;
            gapEnd = gapEnd + count;
            Arrays.fill(elementData, gapStart, gapEnd, null);
        }
        return gapStart;
    }

    private void ensureCapacity(int needed) {
        int left = elementData.length - size();
        if (left <= needed) {
            Object[] newArray = new Object[elementData.length * 2 + needed];
            move(size());
            System.arraycopy(elementData, 0, newArray, 0, elementData.length);
            elementData = newArray;
            gapStart = gapEnd;
            gapEnd = newArray.length - 1;
        }
    }

    @Override
    public boolean add(T t) {
        add(size(), t);
        return true;
    }

    @Override
    public T set(int index, T element) {
        T pre = null;
        if (index < gapStart) {
            pre = (T) elementData[index];
            elementData[index] = element;
        } else {
            int id = gapEnd + index - gapStart + 1;
            pre = (T) elementData[id];
            elementData[id] = element;
        }
        return pre;
    }

    @Override
    public void add(int index, T element) {
        ensureCapacity(1);
        int id = move(index);
        elementData[id] = element;
        gapStart ++;
        size ++;
    }

    @Override
    public T remove(int index) {
        move(index);
        T rmObj = (T) elementData[++gapEnd];
        elementData[gapEnd] = null;
        size --;
        return rmObj;
    }

    /**
     * New API in GapList which supports removing a range of elements in the list
     * @param fromIndex
     * @param toIndex
     */
    public void removeAll(int fromIndex, int toIndex) {
        removeRange(fromIndex, toIndex);
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        ensureCapacity(c.size());
        int id = move(index);
        for (T t : c) {
            elementData[id ++] = t;
        }
        gapStart = id;
        size += c.size();
        return true;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        move(fromIndex);
        Arrays.fill(elementData, gapEnd+1, gapEnd + toIndex - fromIndex + 1, null);
        gapEnd = gapEnd + toIndex - fromIndex;
        size -= toIndex - fromIndex;
    }

    @Override
    public T get(int index) {
        if (index < gapStart) {
            return (T) elementData[index];
        } else {
            int id = gapEnd + index - gapStart + 1;
            return (T) elementData[id];
        }
    }

    @Override
    public int size() {
        return size;
    }
}
