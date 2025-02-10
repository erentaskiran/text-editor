package io.github.some_example_name;
public class Node {

    public char data;
    public char decoration;
    public int charLength;

    public Node(char data, char decoration, int charLength) {
        this.data = data;
        this.decoration = decoration;
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
}
