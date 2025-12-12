package com.jasonhunter95.blackjack;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

/**
 * BlackJack game implementation with a graphical user interface.
 * Provides a complete single-player BlackJack experience against a dealer.
 * 
 * <p>Game Rules:</p>
 * <ul>
 *   <li>Goal: Get as close to 21 as possible without going over</li>
 *   <li>Face cards (J, Q, K) are worth 10</li>
 *   <li>Aces are worth 11 or 1 (automatically adjusted)</li>
 *   <li>Dealer must hit until reaching 17 or higher</li>
 * </ul>
 * 
 * @author Jason Hunter
 * @version 1.0
 */
public class BlackJack {
    // Game constants
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int CARD_WIDTH = 110;
    private static final int CARD_HEIGHT = 154;
    private static final int DEALER_STAND_VALUE = 17;
    private static final int BLACKJACK_VALUE = 21;
    private static final int ACE_REDUCTION = 10;

    // Deck
    private ArrayList<Card> deck;
    private Random random = new Random();

    // Dealer
    private Card hiddenCard;
    private ArrayList<Card> dealerHand;
    private int dealerSum;
    private int dealerAceCount;

    // Player
    private ArrayList<Card> playerHand;
    private int playerSum;
    private int playerAceCount;

    // UI Components
    private JFrame frame = new JFrame("BlackJack");
    private JPanel gamePanel;
    private JPanel buttonPanel = new JPanel();
    private JButton hitButton = new JButton("Hit");
    private JButton stayButton = new JButton("Stay");
    private JButton newGameButton = new JButton("New Game");

    /**
     * Constructs a new BlackJack game and initializes the GUI.
     */
    public BlackJack() {
        startGame();
        initializeUI();
    }

    /**
     * Initializes the game user interface including the game panel and buttons.
     */
    private void initializeUI() {
        gamePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };

        frame.setVisible(true);
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        newGameButton.setFocusable(false);
        newGameButton.setEnabled(false);
        buttonPanel.add(newGameButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(e -> onHit());
        stayButton.addActionListener(e -> onStay());
        newGameButton.addActionListener(e -> onNewGame());

        gamePanel.repaint();
    }

    /**
     * Draws the game state including cards and result messages.
     *
     * @param g The Graphics object to draw with
     */
    private void drawGame(Graphics g) {
        try {
            // Draw hidden card
            Image hiddenCardImg;
            if (!stayButton.isEnabled()) {
                hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
            } else {
                hiddenCardImg = new ImageIcon(getClass().getResource("/cards/BACK.png")).getImage();
            }
            g.drawImage(hiddenCardImg, 20, 20, CARD_WIDTH, CARD_HEIGHT, null);

            // Draw dealer's hand
            for (int i = 0; i < dealerHand.size(); i++) {
                Card card = dealerHand.get(i);
                Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                g.drawImage(cardImg, CARD_WIDTH + 25 + (CARD_WIDTH + 5) * i, 20, CARD_WIDTH, CARD_HEIGHT, null);
            }

            // Draw player's hand
            for (int i = 0; i < playerHand.size(); i++) {
                Card card = playerHand.get(i);
                Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                g.drawImage(cardImg, 20 + (CARD_WIDTH + 5) * i, 320, CARD_WIDTH, CARD_HEIGHT, null);
            }

            // Draw result message when round is over
            if (!stayButton.isEnabled()) {
                dealerSum = reduceAce(dealerSum, dealerAceCount);
                playerSum = reduceAce(playerSum, playerAceCount);

                String message = determineWinner();
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.setColor(Color.white);
                
                // Center the message
                FontMetrics fm = g.getFontMetrics();
                int messageWidth = fm.stringWidth(message);
                int x = (BOARD_WIDTH - messageWidth) / 2;
                g.drawString(message, x, 250);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines the winner based on current hand values.
     *
     * @return A string message indicating the game result
     */
    private String determineWinner() {
        if (playerSum > BLACKJACK_VALUE) {
            return "You Lose!";
        } else if (dealerSum > BLACKJACK_VALUE) {
            return "You Win!";
        } else if (playerSum == dealerSum) {
            return "Tie!";
        } else if (playerSum > dealerSum) {
            return "You Win!";
        } else {
            return "You Lose!";
        }
    }

    /**
     * Handles the Hit button action - draws a card for the player.
     */
    private void onHit() {
        Card card = deck.remove(deck.size() - 1);
        playerSum += card.getValue();
        playerAceCount += card.isAce() ? 1 : 0;
        playerHand.add(card);

        if (reduceAce(playerSum, playerAceCount) > BLACKJACK_VALUE) {
            hitButton.setEnabled(false);
            stayButton.setEnabled(false);
            newGameButton.setEnabled(true);
        }
        gamePanel.repaint();
    }

    /**
     * Handles the Stay button action - dealer draws cards until reaching 17.
     */
    private void onStay() {
        hitButton.setEnabled(false);
        stayButton.setEnabled(false);
        newGameButton.setEnabled(true);

        while (dealerSum < DEALER_STAND_VALUE) {
            Card card = deck.remove(deck.size() - 1);
            dealerSum += card.getValue();
            dealerAceCount += card.isAce() ? 1 : 0;
            dealerHand.add(card);
        }
        gamePanel.repaint();
    }

    /**
     * Handles the New Game button action - resets the game state.
     */
    private void onNewGame() {
        startGame();
        hitButton.setEnabled(true);
        stayButton.setEnabled(true);
        newGameButton.setEnabled(false);
        gamePanel.repaint();
    }

    /**
     * Initializes a new game round by building and shuffling the deck,
     * and dealing initial cards to both dealer and player.
     */
    public void startGame() {
        buildDeck();
        shuffleDeck();

        // Initialize dealer
        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        // Initialize player
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
    }

    /**
     * Builds a standard 52-card deck.
     */
    private void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                deck.add(new Card(value, type));
            }
        }
    }

    /**
     * Shuffles the deck using the Fisher-Yates algorithm.
     */
    private void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
    }

    /**
     * Reduces the value of Aces from 11 to 1 when the hand would otherwise bust.
     *
     * @param sum      The current sum of the hand
     * @param aceCount The number of Aces in the hand
     * @return The adjusted sum after reducing Aces as needed
     */
    private int reduceAce(int sum, int aceCount) {
        while (sum > BLACKJACK_VALUE && aceCount > 0) {
            sum -= ACE_REDUCTION;
            aceCount--;
        }
        return sum;
    }
}
