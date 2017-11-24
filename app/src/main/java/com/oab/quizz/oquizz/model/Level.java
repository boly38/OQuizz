package com.oab.quizz.oquizz.model;

public enum Level {
    BEGINNER(0), INTERMEDIATE(1), EXPERT(2);

    private final int value;

    private Level(int value) {
        this.value = value;
    }

    public static Level fromValue(int level) {
        switch (level) {
            case 0: return BEGINNER;
            case 1: return INTERMEDIATE;
            case 2: return EXPERT;
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}


