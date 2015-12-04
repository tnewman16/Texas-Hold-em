package Poker;

/**
 * Created by ty on 10/6/15.
 * @author Tyler Newman
 */
public class Hand extends Deck {

    /**
     * Extends the Deck class and is used by Player during the game.
     * Each Hand has a maximum size of 2.
     *
     * Instance Variables:
     *     size - the size of the Deck
     *     cards - a list of Cards it contains
     *
     * Methods:
     *     shuffle() - shuffles the Deck
     *     addCard(Card) - adds a new Card to the end of the Deck/Hand
     *     drawCard() - removes a Card from the Deck/Hand
     *     getCards() - returns a list of Card objects
     *     getLength() - returns the length of the Deck/Hand
     *     getCardValues() - returns a list of Card values as strings
     */
    public Hand() {
        super(2);
        this.size = 7;      // sets the size to 7 (would be 0 otherwise) has to account for cards on the table
    }
}
