package io.github.some_example_name;


public class GapBuffer {
    private static final int DEFAULT_SIZE = 10;
    private Node[] buffer;
    private int leftPointer;
    private int rightPointer;
    private int size;
    private int totalWidth;
    private int space=1;
    public GapBuffer() {
        this(DEFAULT_SIZE);
    }

    public GapBuffer(int capacity) {
        buffer = new Node[capacity * 2];
        leftPointer = 0;
        rightPointer = buffer.length - 1;
        size = 0;
    }

    public int getCursor(){
        return leftPointer;
    }

    public void addChar(char c, char decoration, int charLength) {
        if (leftPointer >= rightPointer) {
            expandBuffer();
        }
        buffer[leftPointer++] = new Node(c, decoration, charLength);
        size++;
        totalWidth += charLength + space;
    }

    public void moveCursorLeft() {
        if (leftPointer > 0) {
            buffer[rightPointer--] = buffer[--leftPointer];
            buffer[leftPointer] = null;
        }
    }

    public void moveCursorRight() {
        if (rightPointer < buffer.length - 1) {
            buffer[leftPointer++] = buffer[++rightPointer];
            buffer[rightPointer] = null;
        }
    }

    public void moveCursor(int index) {
        if (index < 0 || index > size) return;
        while (leftPointer > index) moveCursorLeft();
        while (leftPointer < index) moveCursorRight();
    }

    public void remove(int index) {
        if (index < 0 || index >= size) return;

        moveCursor(index+1);

        if (leftPointer > 0) {
            totalWidth -= space + getNode(index).getCharLength();
            leftPointer--;
            buffer[leftPointer] = null;
            size--;
        }

    }

    public Node getNode(int index) {
        if (index < 0 || index >= size) return null;
        return (index < leftPointer) ? buffer[index] : buffer[rightPointer + index - leftPointer + 1];
    }

    public int getSize() {
        return size;
    }

    private void expandBuffer() {
        int newCapacity = buffer.length * 2;
        Node[] newBuffer = new Node[newCapacity];
        System.arraycopy(buffer, 0, newBuffer, 0, leftPointer);
        System.arraycopy(buffer, rightPointer + 1, newBuffer, newCapacity - (buffer.length - rightPointer - 1), buffer.length - rightPointer - 1);
        rightPointer = newCapacity - (buffer.length - rightPointer - 1);
        buffer = newBuffer;
    }

    public int getTotalWidth() {
        return totalWidth;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buffer.length; i++) {
            if (i == leftPointer) sb.append("|");
            sb.append(buffer[i] != null ? buffer[i].getChar() : "_");
        }
        return sb.toString();
    }
}
