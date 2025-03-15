package io.github.some_example_name;
public class Node {

    private char data;
    private boolean bold;
    private boolean italic;
    private boolean underline;

    public Node(char data, boolean bold, boolean italic, boolean underline) {
        this.data = data;
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


    public void setChar(char data) {
        this.data = data;
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
