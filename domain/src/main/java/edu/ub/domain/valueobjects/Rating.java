package edu.ub.domain.valueobjects;

public class Rating {
    private final int value;

    public Rating(int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating must be between 1-5");
        }
        this.value = value;
    }

    public int getValue() { return value; }
}
