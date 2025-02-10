package io.github.some_example_name;
public class Node {

    private char data;
    private char decoration;
    private int charLength;

    public Node(char data, char decoration, int charLength) {
        this.data = data;
        this.decoration = decoration;
        this.charLength = charLength;
    }

    public char getChar() {
        return data;
    }

    public char getDecoration() {
        return decoration;
    }

    public int getCharLength() {
        return charLength;
    }

    public void setChar(char data) {
        this.data = data;
    }
    public void setDecoration(char decoration) {
        this.decoration = decoration;
    }
    public void setCharLength(int charLength) {
        this.charLength = charLength;
    }
}
