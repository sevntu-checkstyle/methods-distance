package net.claribole.zgrviewer.dot;

public class Comment extends Node {

    private String text = "";

    public Comment(Object root) {
        super(root, "comment");
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String toString() {
        return "/*\n" + text + "\n*/\n";
    }
}
