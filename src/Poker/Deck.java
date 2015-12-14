package Poker;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ty on 10/6/15.
 * @author Tyler Newman
 *
 * A class that creates a deck which is used by the table during a game of poker.
 * The deck is given a maximum size which cannot be exceeded.
 */
public class Deck {

    protected int size;
    protected ArrayList<Card> cards;

    /**
     * This construct creates a Deck with a size that is divisible
     * by four (rounds down if input is not divisible by four).
     *
     * @param deckSize - the maximum size of the deck
     */
    public Deck(int deckSize) {
        this.size = deckSize - (deckSize%4);
        this.cards = new ArrayList<>();

        String[] suits = {"S", "D", "H", "C"};
        for (int i = 0; i < deckSize/4; i++) {  // adds specified number of Cards to the Deck
            for (int j=0; j<4; j++) {
                Card newCard = new Card((i % 13) + 2, suits[j]);    // add 4 Cards of Number (i%13 + 2) and each Suit
                cards.add(newCard);
            }
        }
    }

    /**
     * Shuffles all of the cards that are currently in the deck.
     *
     */
    public void shuffle() {
        Random random = new Random();

        for (int i=0; i<cards.size(); i++) {
            int randomPlace = random.nextInt(cards.size() - i); // Choose a random point between 0 and what you haven't looked at
            Card removedCard = cards.remove(randomPlace);   // Remove that random Card
            cards.add(removedCard);     // Place the removed Card at the end of the list
        }
    }

    /**
     * Adds a specific card object to the deck.
     *
     * @param addedCard - the card to be added
     */
    public void addCard(Card addedCard) {
        if ((this.getSize() < size) && (!cards.contains(addedCard))) {
            cards.add(addedCard);
        }

        assert (this.getSize() <= size);    // make sure that the size is less than or equal to the maximum size of the deck
    }

    /**
     * Takes and returns a card object from the top of the deck.
     *
     * @return - the card on the top of the deck
     */
    public Card drawCard() {
        if (this.getSize() != 0) {
            return cards.remove(0);
        } else {
            throw new IndexOutOfBoundsException(    // throws an out of bounds exception if there are no cards in the deck
                    "There are no more cards in the deck!");
        }
    }

    /**
     * Gets the cards in the deck as an ArrayList of card objects.
     *
     * @return - an ArrayList of cards contained within the deck
     */
    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * Gets the cards in the deck as an ArrayList of strings, each containing the card values.
     *
     * @return - an ArrayList of strings indicating which cards are in the deck
     */
    public ArrayList<String> getCardValues() {
        ArrayList<String> cardValues = new ArrayList<>();

        for (Card card : cards) {
            String value = card.getValue();
            cardValues.add(value);
        }

        return cardValues;
    }

    /**
     * Gets the current size of the deck.
     *
     * @return - the deck's current size
     */
    public int getSize() {
        return cards.size();
    }

}
