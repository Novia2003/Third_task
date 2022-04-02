package ru.vsu.cs.novichikhin;

import ru.vsu.cs.util.JTableUtils;

import javax.swing.*;
import java.util.Queue;
import java.util.Stack;

public class FrameMain extends JFrame {
    private JPanel panelMain;
    private JTable tableCardDeck;
    private JButton buttonFirstPlaying;
    private JButton buttonSecondPlaying;
    private JTextField textField;
    private JTable tableFinalDeck;
    private JTable tableCardsOnTable;

    public FrameMain() {
        this.setTitle("Карточная игра");
        this.setContentPane(panelMain);
        this.setBounds(50, 50, 0, 0);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        JTableUtils.initJTableForArray(tableCardDeck, 85, true, true, false, false);
        JTableUtils.initJTableForArray(tableFinalDeck, 85, true, true, false, false);
        JTableUtils.initJTableForArray(tableCardsOnTable, 85, true, true, false, false);

        buttonFirstPlaying.addActionListener(actionEvent -> {
            FirstCardGame game = new FirstCardGame();

            Queue<Card> initialDeck = game.getInitialDeck();
            JTableUtils.writeArrayToJTable(tableCardDeck, initialDeck);

            Queue<Card> finalDeck = game.getFinalDeck();
            JTableUtils.writeArrayToJTable(tableFinalDeck, finalDeck);

            Stack<Card> cardsOnTable = game.getCardsOnTable();
            JTableUtils.writeArrayToJTable(tableCardsOnTable, cardsOnTable);
            int quantityMoves = game.getMove();
            textField.setText(String.valueOf(quantityMoves));

        });

        buttonSecondPlaying.addActionListener(actionEvent -> {
            SecondCardGame game = new SecondCardGame();

            SimpleQueue<Card> initialDeck = game.getInitialDeck();
            JTableUtils.writeArrayToJTable(tableCardDeck, initialDeck);

            SimpleQueue<Card> finalDeck = game.getFinalDeck();
            JTableUtils.writeArrayToJTable(tableFinalDeck, finalDeck);

            SimpleStack<Card> cardsOnTable = game.getCardsOnTable();
            JTableUtils.writeArrayToJTable(tableCardsOnTable, cardsOnTable);
            int quantityMoves = game.getMove();
            textField.setText(String.valueOf(quantityMoves));
        });
    }
}
