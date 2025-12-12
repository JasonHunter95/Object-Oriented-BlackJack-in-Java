package com.jasonhunter95.blackjack;

/**
 * Represents a playing card with a value and suit type.
 * Used in the BlackJack game to manage the deck and hands.
 */
public class Card {
    private String value;
    private String type;

    /**
     * Creates a new Card with the specified value and type.
     *
     * @param value The card value (A, 2-10, J, Q, K)
     * @param type  The card suit (C=Clubs, D=Diamonds, H=Hearts, S=Spades)
     */
    public Card(String value, String type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Returns a string representation of the card in "value-type" format.
     *
     * @return The card as a string (e.g., "A-H" for Ace of Hearts)
     */
    @Override
    public String toString() {
        return value + "-" + type;
    }

    /**
     * Calculates the numerical value of the card for BlackJack scoring.
     * Face cards (J, Q, K) are worth 10, Aces are worth 11 (can be reduced later),
     * and number cards are worth their face value.
     *
     * @return The card's point value
     */
    public int getValue() {
        if ("AJQK".contains(value)) {
            if (value.equals("A")) {
                return 11;
            }
            return 10;
        }
        return Integer.parseInt(value);
    }

    /**
     * Checks if this card is an Ace.
     *
     * @return true if the card is an Ace, false otherwise
     */
    public boolean isAce() {
        return value.equals("A");
    }

    /**
     * Gets the file path to the card's image resource.
     *
     * @return The relative path to the card image
     */
    public String getImagePath() {
        return "/cards/" + toString() + ".png";
    }
}
