package edu.ub.domain.valueobjects;

public class Comment {
    private final String text;

    public Comment(String text) {
        if (text.length() > 500) {
            throw new IllegalArgumentException("Comment too long");
        }
        this.text = text;
    }

    public String getText() { return text; }

}
