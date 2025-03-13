package io.github.some_example_name;


import java.util.ArrayList;
import java.util.List;

public class GapBuffer {
    private static final int DEFAULT_SIZE = 10;
    private Node[] buffer;
    private int leftPointer;
    private int rightPointer;
    private int size;
    private List<Integer> lineBreaks = new ArrayList<>();

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

    public void addChar(char c, boolean bold, boolean italic, boolean underline) {
        if (leftPointer >= rightPointer) {
            expandBuffer();
        }
        buffer[leftPointer++] = new Node(c, bold, italic, underline);
        size++;
        if (c == '\n') {
            lineBreaks.add(leftPointer);
        }
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

    public void moveCursorUp() {
        if (lineBreaks.isEmpty() || leftPointer == 0) return;

        int currentLine = getCurrentLine();
        if (currentLine == 0) {
            moveCursor(0);
            return;
        }

        int previousLineStart = (currentLine == 1) ? 0 : lineBreaks.get(currentLine - 2);
        int previousLineEnd = lineBreaks.get(currentLine - 1);
        int currentColumn = leftPointer - lineBreaks.get(currentLine - 1);

        int newCursorPos = Math.min(previousLineStart + currentColumn, previousLineEnd - 1);
        moveCursor(newCursorPos);
    }

    public void moveCursorDown() {
        if (lineBreaks.isEmpty() || leftPointer == size) return;

        int currentLine = getCurrentLine();
        if (currentLine >= lineBreaks.size() - 1) {
            moveCursor(size);
            return;
        }

        int nextLineStart = lineBreaks.get(currentLine);
        int nextLineEnd = (currentLine == lineBreaks.size() - 1) ? size : lineBreaks.get(currentLine + 1);
        int currentColumn = leftPointer - (currentLine == 0 ? 0 : lineBreaks.get(currentLine - 1));

        int newCursorPos = Math.min(nextLineStart + currentColumn, nextLineEnd - 1);
        moveCursor(newCursorPos);
    }

    private int getCurrentLine() {
    for (int i = 0; i < lineBreaks.size(); i++) {
        if (leftPointer < lineBreaks.get(i)) {
            return i;
        }
    }
    return lineBreaks.size();
}
    public void moveCursor(int index) {
        if (index < 0 || index > size) return;
        while (leftPointer > index) moveCursorLeft();
        while (leftPointer < index) moveCursorRight();
    }

    public void remove(int index) {
        if (index < 0 || index >= size) return;

        moveCursor(index + 1);

        if (leftPointer > 0) {
            if(lineBreaks.contains(leftPointer - 1)){
                removeLineBreak(leftPointer - 1);
            }
            leftPointer--;
            buffer[leftPointer] = null;
            size--;

        }
    }

    public void addLineBreak() {
        lineBreaks.add(leftPointer);
    }

    private void removeLineBreak(int index) {
        for (int i = 0; i < lineBreaks.size(); i++) {
            if (lineBreaks.get(i) == index) {
                lineBreaks.remove(i);
                break;
            }
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

    public void changeDecoration(int index, boolean bold, boolean italic, boolean underline) {
        if (index < 0 || index >= size) return;
        Node node = getNode(index);
        node.setBold(bold);
        node.setItalic(italic);
        node.setUnderline(underline);
    }

    public List<List<Node>> getLines() {
        List<List<Node>> lines = new ArrayList<>();
        int start = 0;

        for(int e: lineBreaks){
            lines.add(getSubstring(start, e));
            start = e;
        }

        if (start < size) {
            lines.add(getSubstring(start, size));
        }
        return lines;
    }

    private List<Node> getSubstring(int start, int end) {
        List<Node> line = new ArrayList<>();
        for (int i = start; i < end; i++) {
            Node node = getNode(i);
            if (node != null) {
                line.add(node);
            }
        }
        return line;
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
