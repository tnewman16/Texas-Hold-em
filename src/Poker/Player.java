package Poker;

/**
 * Created by ty on 10/6/15.
 * @author Tyler Newman
 *
 * A class that creates an instance of a Player to be used when playing a game of poker.
 * Takes the player's name and the amount of money they will start off with.
 * Gives each player their own Hand, Wallet, and record.
 */
public class Player {

    private String name;
    private Hand hand = new Hand();
    private Wallet wallet;
    private int wins = 0;
    private int losses = 0;
    private boolean folded;
    private double currentBet;

    public Player(String playerName, double money) {
        if (playerName.length() > 9) {
            throw new IllegalArgumentException("Name must be less than 10 characters");
        }
        this.name = playerName;
        this.wallet = new Wallet(money);
        this.folded = false;
        this.currentBet = 0.0;
    }

    /**
     * Takes in a Card object and adds it to the player's hand.
     *
     * @param card - the card to be added to the player's hand
     */
    public void giveCard(Card card) {
        hand.addCard(card);
    }

    public Card takeCard() {
        Card card = null;
        if (hand.getSize() != 0) {
            try {
                card = hand.drawCard();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else {
            throw new IndexOutOfBoundsException();  // throws an out of bounds exception if the hand has 0 cards
        }
        return card;
    }

    /**
     * Adds a win to the player's overall record and adds the amount of money they won to their wallet.
     *
     * @param moneyWon - the amount of money that the player has won
     */
    public void addWin(double moneyWon) {
        wallet.addMoney(moneyWon);
        wins += 1;
    }

    /**
     * Adds a loss to the player's overall record.
     *
     */
    public void addLoss() {
        losses += 1;
    }

    /**
     * Gets the overall record for the player.
     *
     * @return - the record of the player
     */
    public String getRecordString() {
        return (wins + "-" + losses);
    }

    /**
     * Gets the name of the player.
     *
     * @return - the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Subtracts the amount a player has bet from their wallet.
     *
     * @param amount - the amount of money that the player has bet
     */
    public void bet(double amount) {
        if (!wallet.isEmpty()) {
            wallet.subtractMoney(amount);
            currentBet += amount;
        }
    }

    /**
     * Resets the overall bet amount for the player to be used in a new round.
     *
     */
    public void resetCurrentBet() {
        currentBet = 0.0;
    }

    /**
     * Sets the folded boolean to true, indicating that the player has folded.
     *
     */
    public void fold() {
        folded = true;
    }

    /**
     * Sets the folded boolean to false, indicating that the player has unfolded.
     *
     */
    public void unfold() {
        folded = false;
    }

    /**
     * Gets whether or not the player is currently folded.
     *
     * @return - a boolean indicating if the player is folded.
     */
    public boolean isFolded() {
        return folded;
    }

    /**
     * Gets the overall balance of the player's wallet.
     *
     * @return - the balance of the player's wallet
     */
    public double getBalance() {
        return wallet.getBalance();
    }

    /**
     * Gets the overall bet for the player to be used during the current round.
     *
     * @return - the current overall bet for the player.
     */
    public double getCurrentBet() {
        return currentBet;
    }

    /**
     * Gets the hand of the player.
     *
     * @return - the player's hand
     */
    public Hand getHand() {
        return hand;
    }
}
