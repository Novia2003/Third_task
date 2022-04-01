package ru.vsu.cs.novichikhin;

import java.util.*;

public class SecondCardGame {
    private SimpleQueue<Card> initialDeck;
    private SimpleStack<Card> cardsOnTable;
    private int move;
    private SimpleLinkedListQueue<Card> finalDeck;

    public SecondCardGame() {
        creatingAndFillingDeckCard();
        playing();
    }

    public void creatingAndFillingDeckCard() {
        String[] suits = new String[]{"spades", "clubs", "diamonds", "hearts"}; //пики, крести, бубны, червы
        String[] names = new String[]{"6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        List<Card> cards = new ArrayList<>();
        for (String suit : suits) {
            for (String name : names) {
                cards.add(new Card(name, suit));
            }
        }

        Random rnd = new Random();
        for (int i = 0; i < cards.size(); i++) {
            int randomIndex = rnd.nextInt(i + 1);
            Card randomCard = cards.get(randomIndex);
            cards.set(randomIndex, cards.get(i));
            cards.set(i, randomCard);
        }

        initialDeck = new SimpleLinkedListQueue<>();
        for (Card card : cards) {
            initialDeck.add(card);
        }

        finalDeck = new SimpleLinkedListQueue<>();
        for (Card card : cards) {
            finalDeck.add(card);
        }
    }

    public void playing() {
        move = 0;
        cardsOnTable = new SimpleLinkedListStack<>();
        cardsOnTable.push(finalDeck.poll());

        Card firstUnsuitableCard = null;
        boolean weHaveNotThisCardYet = true;

        while (finalDeck.count() != 0) {
            Card cardInDeck = finalDeck.element();
            Card cardOnTable = cardsOnTable.peek();

            if (cardInDeck.getSuit().equals(cardOnTable.getSuit()) || cardInDeck.getName().equals(cardOnTable.getName())) {
                cardsOnTable.push(cardInDeck);
                firstUnsuitableCard = null;
                weHaveNotThisCardYet = true;
            } else {
                if (firstUnsuitableCard == cardInDeck) {
                    break;
                }

                if (weHaveNotThisCardYet) {
                    firstUnsuitableCard = cardInDeck;
                }

                finalDeck.add(cardInDeck);
                weHaveNotThisCardYet = false;
            }

            finalDeck.poll();
            move++;
        }
    }

    public SimpleQueue<Card> getInitialDeck() {
        return initialDeck;
    }

    public SimpleStack<Card> getCardsOnTable() {
        return cardsOnTable;
    }

    public int getMove() {
        return move;
    }

    public SimpleQueue<Card> getFinalDeck() {
        return finalDeck;
    }
}