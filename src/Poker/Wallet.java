package Poker;

/**
 * Created by ty on 10/6/15.
 * @author Tyler Newman
 */
public class Wallet {

    private double balance;
    private boolean empty = false;

    /**
     * This constructor creates a Wallet with a specified amount.
     */
    public Wallet(double amount) {
        this.balance = amount;
        if (this.balance == 0) {
            empty = true;
        }
    }

    public void addMoney(double amount) {
        balance += amount;
    }

    public int subtractMoney(double amount) {
        if (balance - amount > 0) {
            balance -= amount;
            return 0;       // 0 = SUCCESS
        }

        balance = 0;    // Balance cannot be negative
        empty = true;   // Sets the Wallet's empty status to true
        return 1;       // 1 = FAILURE
    }

    public double getBalance() {
        return balance;
    }

    public boolean isEmpty() {
        return empty;
    }
}
