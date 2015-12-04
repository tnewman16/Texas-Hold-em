package Poker;

/**
 * Created by ty on 10/6/15.
 * @author Tyler Newman
 */

public class Card {
    private final String cardValue;

    /**
     * This constructor creates a Card with a specified number and suit.
     */
    public Card(int cardNumber, String cardSuit) {
        String value;
        if (cardNumber == 11) {
            value = "J";
        } else if (cardNumber == 12) {
            value = "Q";
        } else if (cardNumber == 13) {
            value = "K";
        } else if (cardNumber == 14) {
            value = "A";
        } else {
            value = String.valueOf(cardNumber);
        }
        this.cardValue = value + "-" + cardSuit;
    }

    public String getValue() {
        return cardValue;
    }

}
