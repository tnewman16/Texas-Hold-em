package Poker;

/**
 * Created by ty on 10/6/15.
 */
public class Blind {

    private double bigBlind;
    private double smallBlind;
    private int bigPosition;
    private int smallPosition;
    private int numOfPlayers;

    public Blind(double big, double small, int players) {
        this.bigBlind = big;
        this.smallBlind = small;

        this.bigPosition = 0;
        this.smallPosition = (bigPosition + 1) % players;

        this.numOfPlayers = players;
    }

    public void advance() {
        bigPosition = (bigPosition + 1) % numOfPlayers;
        smallPosition = (smallPosition + 1) % numOfPlayers;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public double getSmallBlind() {
        return smallBlind;
    }

    public int getBigPosition(){
        return bigPosition;
    }

    public int getSmallPosition() {
        return smallPosition;
    }
}
