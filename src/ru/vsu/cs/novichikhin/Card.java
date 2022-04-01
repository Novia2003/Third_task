package ru.vsu.cs.novichikhin;

public class Card {
    private String suit;
    private String name;

    public String getSuit() {
        return suit;
    }

    public String getName() {
        return name;
    }

    public Card(String name, String suit) {
        this.name = name;
        this.suit = suit;
    }
}
