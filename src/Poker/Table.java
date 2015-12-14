package Poker;

import java.util.*;

/**
 * Created by ty on 10/6/15.
 * @author Tyler Newman
 */
public class Table {

    private ArrayList<Player> players;      // stores all the players
    private ArrayList<Card> cardsOnTable;   // the cards that have been dealt
    private Deck deck;
    private HandChecker handChecker;
    private double pot = 0;
    private int numOfFoldedPlayers = 0;     // keeps track of who has folded during one round


    private Controller controller;

    public Table(Controller cont) {
        controller = cont;
        players = new ArrayList<>();
        cardsOnTable = new ArrayList<>();
        handChecker = new HandChecker(players);
        deck = new Deck(52);
        deck.shuffle();

        double startingMoney = 1000;

        // Establish Blinds for the Game
        double bigBlind = (int) startingMoney / 20;
        if (bigBlind > 50) {
            bigBlind = 50;
        } else if (bigBlind < 10) {
            bigBlind = 10;
        }
        double smallBlind = (int) bigBlind / 2;

        setPlayers(controller.getPlayerNames(), startingMoney);

    }

    public void setPlayers(ArrayList<String> names, double startingMoney) {
        for (String name : names) {
            try {
                Player player = new Player(name, startingMoney);
                players.add(player);
            } catch (IllegalArgumentException e) {
                /* no-op */
            }

        }
    }

    public void dealCardsToTable(int time) {    // time is which part of the deal it is on (0 = first, 1 = second, and 1 = third)
        if (time == 0) {
            for (int i=0; i<3; i++) {       // if it is the first time, deal three cards to the table
                try {
                    cardsOnTable.add(deck.drawCard());
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        } else if (time == 1 || time == 2) {    // if it is the second or third time, deal one card to the table
            try {
                cardsOnTable.add(deck.drawCard());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public void dealCardsToPlayers() {
        for (int i=0; i<players.size()*2; i++) {
            try {
                players.get(i % players.size()).giveCard(deck.drawCard());    // Deals one Card from the Deck to each Player twice
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public void takeCardsFromTable() {
        int size = cardsOnTable.size();
        for (int i=0; i<size; i++) {
            deck.addCard(cardsOnTable.remove(0));
        }
    }

    public void takeCardsFromPlayers() {
        for (int i = 0; i < players.size()*7; i++) {
            try {
                deck.addCard(players.get(i % players.size()).takeCard());
            } catch (IndexOutOfBoundsException e) {
                /* no-op */
            }
        }
    }

    /**
     * Resets the current bet for each player so that a new a new betting round may be started.
     * Is used when a betting round has been finished.
     */
    public void resetAllBets() {
     for(Player player : players) {
            player.resetCurrentBet();
        }
    }


    public int[] endGame() {
        int[] winningPositions = handChecker.determineWinner(numOfFoldedPlayers, cardsOnTable);

        ArrayList<Integer> positionsWon = new ArrayList<>();    // keeps track of who has won

        // add all of the wins (could be multiple for a tie)
        for (int pos : winningPositions) {
            double moneyWon = pot / winningPositions.length;
            players.get(pos).addWin(moneyWon);
            positionsWon.add(pos);
        }

        // add all of the losses
        for (int i=0; i< players.size(); i++) {
            if (!positionsWon.contains(i)) {    // if the player has NOT already won
                players.get(i).addLoss();
            }
        }

        return winningPositions;
    }

    /**
     * Resets all of the players' folded booleans to false.
     * Is used when a round of poker has been finished.
     */
    public void resetGame() {
        // reset player folds
        for (Player player : players) {
            player.unfold();
        }

        // reset all instance variables to get ready for a new round
        numOfFoldedPlayers = 0;
        pot = 0.0;
        takeCardsFromPlayers();
        takeCardsFromTable();
    }

    public void shuffleDeck() {
        deck.shuffle();
    }

    public Player getPlayer(int position) {
        return players.get(position);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public double getPot() {
        return pot;
    }

    public void changePot(double amount) {
        pot += amount;
    }

    public int getNumOfFoldedPlayers() {
        return numOfFoldedPlayers;
    }

    public ArrayList<Card> getCardsOnTable() {
        return cardsOnTable;
    }

}
