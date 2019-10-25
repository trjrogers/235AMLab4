package com.murach.piggame;

public class Dice {
    private Integer maxSides;

    public Dice(int sides) {
        this.maxSides = sides;
    }

    public int Roll() {
        return (int)(Math.random() * (maxSides) + 1);
    }
}