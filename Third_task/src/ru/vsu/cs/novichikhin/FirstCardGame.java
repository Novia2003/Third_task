package ru.vsu.cs.novichikhin;

import java.util.*;

public class FirstCardGame {
    private Queue<Card> initialDeck;
    private Stack<Card> cardsOnTable;
    private int move = 0;
    private Queue<Card> finalDeck;

    public FirstCardGame() {
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

        initialDeck = new LinkedList<>(cards);
        finalDeck = new LinkedList<>(cards);
    }

    public void playing() {
        cardsOnTable = new Stack<>();
        cardsOnTable.push(finalDeck.poll());

        Card firstUnsuitableCard = null;
        boolean weHaveNotThisCardYet = true;

        while (finalDeck.size() != 0) {
            Card cardInDeck = finalDeck.peek();
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

                finalDeck.offer(cardInDeck);
                weHaveNotThisCardYet = false;
            }

            finalDeck.poll();
            move++;
        }
    }

    public Queue<Card> getInitialDeck() {
        return initialDeck;
    }

    public Stack<Card> getCardsOnTable() {
        return cardsOnTable;
    }

    public int getMove() {
        return move;
    }

    public Queue<Card> getFinalDeck() {
        return finalDeck;
    }
}
