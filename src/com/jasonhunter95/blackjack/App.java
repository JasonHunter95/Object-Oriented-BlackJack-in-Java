package com.jasonhunter95.blackjack;

/**
 * Application entry point for the BlackJack game.
 */
public class App {
    /**
     * Main method that launches the BlackJack game.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new BlackJack());
    }
}
