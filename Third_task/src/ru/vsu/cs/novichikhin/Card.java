package ru.vsu.cs.novichikhin;

public record Card(String name, String suit) {
    public String getSuit() {
        return suit;
    }

    public String getName() {
        return name;
    }
}
