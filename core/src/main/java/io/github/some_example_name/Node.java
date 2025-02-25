package io.github.some_example_name;
public class Node {

    private char data;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private int charLength;

    public Node(char data, int charLength, boolean bold, boolean italic, boolean underline) {
        this.data = data;
        this.charLength = charLength;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
    }

    public char getChar() {
        return data;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public int getCharLength() {
        return charLength;
    }

    public void setChar(char data) {
        this.data = data;
    }

    public void setCharLength(int charLength) {
        this.charLength = charLength;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }
}
