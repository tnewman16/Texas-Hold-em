package Poker;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {

    @FXML private Label commandsLabel;
    @FXML private Label raiseLabel;
    @FXML private Label topLabel;
    @FXML private Label highBetLabel;
    @FXML private Label potLabel;
    @FXML private Button playGameButton;
    @FXML private Button quitGameButton;
    @FXML private Button betButton;
    @FXML private Button callButton;
    @FXML private Button foldButton;
    @FXML private Button flipButton;
    @FXML private Label handNameLabel;

    @FXML private Label liveInfoLabel;
    @FXML private TextField liveTextField;
    @FXML private Label playerNameLabel;
    @FXML private Label playerWalletLabel;
    @FXML private Label playerRecordLabel;
    @FXML private Label statRoundsPlayed;
    @FXML private Label statHighestPot;
    @FXML private Label statBestRecord;

    @FXML private TextField betTextField;
    @FXML private Label betErrorLabel;
    @FXML private ArrayList<ImageView> tableCards;
    @FXML private ArrayList<ImageView> handCards;

    private Table table;
    private Blind blind;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<Player> players;
    private int tablePosition;
    private double currentHighBet = 0.0;
    private double roundHighBet = 50.0;
    private String highestBettersName;
    private int timeInRound = -1;
    private boolean flipped = false;
    private int numOfFoldedPlayers;
    private boolean initial = true;
    private int roundsPlayedInGame = 0;


    public void getName(ActionEvent actionEvent) {
        String name = liveTextField.getText();
        liveTextField.setText("");

        if (name.length() > 9) {
            liveInfoLabel.setText("Please enter a name that is less than 10 characters.");

        } else if (!names.contains(name) && name.length() > 0) {
            names.add(name);
            betErrorLabel.setText("");
            liveInfoLabel.setText("Welcome to Poker! Enter the names of each player one at a time. Click play when ready.");

        } else {
            liveInfoLabel.setText("Please enter a name that hasn't been entered.");
        }

        if (names.size() > 1) {
            playGameButton.setDisable(false);
        }

    }

    public void setLiveInput(String text) {
        liveInfoLabel.setText(text);
    }

    /**
     * Called when the BET button is pressed.
     *
     * @param event - the action on the button
     */
    public void performCommand(ActionEvent event) {
        Button buttonPressed = (Button) event.getSource();
        String buttonLabel = buttonPressed.getText();

        liveInfoLabel.setText("");

        Player currentPlayer = players.get(tablePosition);

        if (buttonLabel.equals("Raise")) {
            Double bet = null;

            try {
                String input = betTextField.getText();
                bet = Double.parseDouble(input);    // may throw exception

                double total = (currentHighBet - currentPlayer.getCurrentBet()) + bet;
                if (currentPlayer.getBalance() >= total) {      // if the user has enough money
                    currentPlayer.bet(total);
                    table.changePot(total);
                    currentHighBet += bet;
                    roundHighBet += bet;
                    highestBettersName = currentPlayer.getName();

                } else {
                    betErrorLabel.setVisible(true);
                    betErrorLabel.setText("Please enter a value you can bet.");
                }

            } catch (IllegalArgumentException e) {
                betTextField.setText("");
                betErrorLabel.setVisible(true);
                betErrorLabel.setText("Please enter a number value.");
            }
            if (bet != null) {    // if a number was entered
                betTextField.setText("");
                betErrorLabel.setText("");
                betErrorLabel.setVisible(false);

            }

        } else if (buttonLabel.equals("Call")) {

            double total = currentHighBet - currentPlayer.getCurrentBet();
            if (currentPlayer.getCurrentBet() < currentHighBet
                    && currentPlayer.getBalance() >= total) {   // if it is not the highest better and they have enough money
                currentPlayer.bet(total);
                table.changePot(total);
            }

            if (highestBettersName.equals("")) {
                // if the highest better folded, this player is now the highest better
                highestBettersName = currentPlayer.getName();
            }

        } else if (buttonLabel.equals("Fold")) {
            currentPlayer.fold();
            numOfFoldedPlayers++;
            if (currentPlayer.getName().equals(highestBettersName)) {
                // make the highest better nobody
                highestBettersName = "";
            }
        }

        updateCurrentStats(timeInRound == 3);

        // update position
        boolean done = false;
        while (!done) {
            tablePosition = (tablePosition + 1) % players.size();
            if (!players.get(tablePosition).isFolded()) {   // if the next player isn't folded
                done = true;
            }
            updateBoard(0);
        }

        // update time in round
        if (players.get(tablePosition).getName().equals(highestBettersName) || (players.size() - numOfFoldedPlayers) == 1) {
            timeInRound++;
            advanceRound();
        }

    }

    public void advanceRound() {

        if (timeInRound == 3 || (players.size() - numOfFoldedPlayers) == 1) {
            // end a round
            int[] winners = table.endGame();
            if (winners.length > 1) {     // if it is a tie
                String text = "The round has ended in a tie between ";
                for (int i=0; i<winners.length; i++) {
                    if (i < winners.length - 1) {
                        text += players.get(winners[i]).getName() + ", ";
                    } else {
                        text += "and " + players.get(winners[i]).getName() + ".";
                    }
                }
                liveInfoLabel.setText(text);

            } else {
                liveInfoLabel.setText("Congratulations " + players.get(winners[0]).getName() +
                        ", you have won the round and $" + table.getPot() + "!");

            }

            roundsPlayedInGame++;
            roundHighBet = 50.0;
            updateBoard(winners[0]);
            updateCurrentStats(true);   // it is the end of the round, so give it true
            disarmCommands();

        } else if (timeInRound == -1) {
            // begin a round
            table.shuffleDeck();
            table.dealCardsToPlayers();
            table.dealCardsToTable(timeInRound);
            getBlindsForRound();

            updateBoard(0);
            updateCurrentStats(false);  // not the end of the round yet, so give it false
            armCommands();

        } else if (timeInRound < 3) {
            // advance the round otherwise
            table.dealCardsToTable(timeInRound);
            roundHighBet = 0.0;
            updateBoard(0);
            updateCurrentStats(false);  // not the end of the round yet, so give it false
            armCommands();
        }

    }

    public void startGame(MouseEvent event) {

        if (names.size() > 1 && initial) {      // if it is the beginning of an entire game
            table = new Table(this);
            players = table.getPlayers();
            numOfFoldedPlayers = table.getNumOfFoldedPlayers();
            initial = false;

            double startingMoney = 1000.0;
            makeBlinds(startingMoney);      // create the blinds

            liveInfoLabel.setText("");
            betErrorLabel.setText("");
            liveTextField.setVisible(false);
            playGameButton.setVisible(false);
            quitGameButton.setVisible(false);

            advanceRound();

        } else if (names.size() > 1 && !initial) {      // if it is the beginning of another round
            // reset everything back to normal
            table.resetAllBets();
            table.resetGame();

            liveInfoLabel.setText("");
            betErrorLabel.setText("");
            liveTextField.setVisible(false);
            playGameButton.setVisible(false);
            quitGameButton.setVisible(false);
            timeInRound = -1;

            // reset the cards on the board
            for (int i=0; i<5; i++) {
                if (i < 2) {
                    handCards.get(i).setImage(new Image(getClass().getResource("cards/card_back.png").toExternalForm()));
                }
                tableCards.get(i).setImage(new Image(getClass().getResource("cards/card_back.png").toExternalForm()));
            }

            changeFlip();

            blind.advance();
            advanceRound();
        }

    }

    public void quitGame(Event event) {

        table.resetAllBets();
        table.resetGame();
        timeInRound = -1;
        initial = true;
        names = new ArrayList<>();
        roundsPlayedInGame = 0;

        setPotLabel("$0");
        setHighBetLabel("$0");
        disarmCommands();
        playGameButton.setDisable(true);
        changeFlip();
        disarmFlipButton();
        setTopLabel("Welcome to Texas Hold'em!");
        setLiveInput("Welcome to Poker! Enter the names of each player one at a time. Click play when ready.");
        liveTextField.setVisible(true);
        quitGameButton.setVisible(false);
        setPlayerInfo("~","~","~");

        for (int i=0; i<5; i++) {
            if (i<2) {
                handCards.get(i).setImage(new Image(getClass().getResource("cards/card_back.png").toExternalForm()));
                tableCards.get(i).setImage(new Image(getClass().getResource("cards/card_back.png").toExternalForm()));
            } else {
                tableCards.get(i).setImage(new Image(getClass().getResource("cards/card_back.png").toExternalForm()));
            }
        }
    }


    public void updateBoard(int winningPosition) {
        ArrayList<Card> cardsOnTable = table.getCardsOnTable();
        setPotLabel("$" + table.getPot());
        setHighBetLabel("$" + roundHighBet);

        Player currentPlayer = table.getPlayer(tablePosition);
        String name = currentPlayer.getName();

        setPlayerInfo(name, "$" + currentPlayer.getBalance(), currentPlayer.getRecordString());
        setTopLabel(name + ", it's your turn. Choose a command!");

        // show cards on the table
        for (int i=0; i<cardsOnTable.size(); i++) {
            String value = cardsOnTable.get(i).getValue();
            tableCards.get(i).setImage(new Image(getClass().getResource("cards/" + value + ".png").toExternalForm()));
        }

        if (timeInRound != 3) {     // if it's not the last time in the round
            // show the player's cards' backs
            handCards.get(0).setImage(new Image(getClass().getResource("cards/card_back.png").toExternalForm()));
            handCards.get(1).setImage(new Image(getClass().getResource("cards/card_back.png").toExternalForm()));
            armFlipButton();

        } else {    // if it's the last time in the round
            tablePosition = winningPosition;
            changeFlip();
            handNameLabel.setText(players.get(tablePosition).getName() + ":");
            setPlayerInfo(players.get(tablePosition).getName(), "$" + players.get(tablePosition)
                    .getBalance(), players.get(tablePosition).getRecordString());
            displayCards();

            topLabel.setText("To play another round, click the Play button!");
            playGameButton.setVisible(true);
            quitGameButton.setVisible(true);
        }

    }

    public void makeBlinds(double startingMoney) {
        // Establish Blinds for the Game
        double bigBlind = (int) startingMoney / 20;
        if (bigBlind > 50) {
            bigBlind = 50;
        } else if (bigBlind < 10) {
            bigBlind = 10;
        }
        double smallBlind = (int) bigBlind / 2;

        blind = new Blind(bigBlind, smallBlind, players.size());

    }

    public void getBlindsForRound() {
        double big = blind.getBigBlind();
        double small = blind.getSmallBlind();
        int smallPos = blind.getSmallPosition();
        int bigPos = blind.getBigPosition();

        Player bigPlayer = players.get(bigPos);
        bigPlayer.bet(big);       // bet the BIG BLIND
        players.get(smallPos).bet(small);   // bet the SMALL BLIND

        highestBettersName = bigPlayer.getName();

        // change the pot and highest bet
        table.changePot(big);
        table.changePot(small);
        currentHighBet = big;

        // update the position
        tablePosition = smallPos;
        liveInfoLabel.setText(players.get(bigPos).getName() + " has bet the Big Blind of $" + big + "\n" +
                players.get(smallPos).getName() + ", you have bet the Small Blind of $" + small);

        updateBoard(0);
    }


    public void flipCards(MouseEvent event) {

        if (flipButton.getText().equals("Flip Cards")) {
            if (flipped) {
                handCards.get(0).setImage(new Image(getClass().getResource("cards/card_back.png").toExternalForm()));
                handCards.get(1).setImage(new Image(getClass().getResource("cards/card_back.png").toExternalForm()));

            } else {
                displayCards();
            }

            flipped = !flipped;
        } else {
            tablePosition = (tablePosition + 1) % players.size();
            handNameLabel.setText(players.get(tablePosition).getName() + ":");
            setPlayerInfo(players.get(tablePosition).getName(), "$" + players.get(tablePosition).getBalance(), players.get(tablePosition).getRecordString());
            displayCards();

        }

    }

    public void displayCards() {
        Player player = players.get(tablePosition);
        String value1 = player.getHand().getCards().get(0).getValue();
        String value2 = player.getHand().getCards().get(1).getValue();

        handCards.get(0).setImage(new Image(getClass().getResource("cards/" + value1 + ".png").toExternalForm()));
        handCards.get(1).setImage(new Image(getClass().getResource("cards/" + value2 + ".png").toExternalForm()));
    }

    public void changeFlip() {

        if (flipButton.getText().equals("Flip Cards")) {
            flipButton.setText("Next Hand");

        } else {
            flipButton.setText("Flip Cards");
            handNameLabel.setText("");
        }
    }

    public void armFlipButton() {
        flipButton.setDisable(false);
    }

    public void disarmFlipButton() {
        flipButton.setDisable(true);
    }

    /**
     * Arms all of the buttons in the Commands section so that they can be used.
     *
     */
    public void armCommands() {
        betButton.setDisable(false);
        callButton.setDisable(false);
        foldButton.setDisable(false);
        betTextField.setDisable(false);
        raiseLabel.setDisable(false);
        commandsLabel.setDisable(false);
    }

    /**
     * Disarms all of the buttons in the Commands section so that they cannot be used.
     *
     */
    public void disarmCommands() {
        betButton.setDisable(true);
        callButton.setDisable(true);
        foldButton.setDisable(true);
        betTextField.setDisable(true);
        raiseLabel.setDisable(true);
        commandsLabel.setDisable(true);

    }

    public void setStatInfo(String stats) {
        String[] statList = stats.split(" ");
        statRoundsPlayed.setText(statList[0]);
        statHighestPot.setText("$" + statList[1]);
        statBestRecord.setText(statList[2]);

    }

    public void updateCurrentStats(boolean endOfRound) {
        boolean roundsChanged = false;
        boolean potChanged = false;
        boolean betChanged = false;

        // first check the number of current number of rounds played
        int previousRoundsPlayed = Integer.parseInt(statRoundsPlayed.getText());
        if (roundsPlayedInGame > previousRoundsPlayed) {
            statRoundsPlayed.setText(String.valueOf(roundsPlayedInGame));
            roundsChanged = true;
        }

        // second check the value of the current pot
        double highestPotInGame = table.getPot();
        String previousPotString = statHighestPot.getText();
        double previousPotHigh = Double.parseDouble(previousPotString.substring(1,previousPotString.length()));
        if (highestPotInGame > previousPotHigh) {
            statHighestPot.setText("$" + String.valueOf(highestPotInGame));
            potChanged = true;
        }

        // third check the highest record among the current players ONLY IF it is at the end of the round
        if (endOfRound) {
            int highPosition = 0;
            String highestRecord = players.get(0).getRecordString();
            String[] splitList = highestRecord.split("-");
            // the value of the record = wins / losses
            double highestRecordValue = (Integer.parseInt(splitList[0]) / (Integer.parseInt(splitList[1])+1));
            for (int i = 1; i < players.size(); i++) {
                String nextRecord = players.get(i).getRecordString();
                String[] splitList2 = nextRecord.split("-");
                double nextRecordValue = (Integer.parseInt(splitList2[0]) / (Integer.parseInt(splitList2[1])+1));

                // if the next player has a better record
                if (nextRecordValue > highestRecordValue) {
                    highestRecordValue = nextRecordValue;
                    highPosition = i;
                }
            }
            String previousHigh = statBestRecord.getText();
            String[] splitList3 = previousHigh.split("-");
            double previousHighValue = (Integer.parseInt(splitList3[0]) / Integer.parseInt((splitList3[1])+1));

            String bestRecordInGame = players.get(highPosition).getRecordString();

            if (highestRecordValue > previousHighValue) {
                statBestRecord.setText(bestRecordInGame);
                betChanged = true;
            }
        }

        if (roundsChanged || potChanged || betChanged) {     // if there have been any changes to the records, write them to the file
            writeStatsToFile();
        }

    }

    public void writeStatsToFile() {
        String stats = statRoundsPlayed.getText()+"\n" + statHighestPot.getText().split("\\$")[1]+"\n" + statBestRecord.getText();
        try {
            // create the file printer for the controller to use later
            File file = new File("poker_stats.txt");
            FileOutputStream statWriter = new FileOutputStream(file);

            statWriter.write(stats.getBytes());

        } catch (IOException e) {
            System.err.println("Could not write to stats file...");
        }
    }

    public void setPlayerInfo(String player, String wallet, String record) {
        playerNameLabel.setText(player);
        playerWalletLabel.setText(wallet);
        playerRecordLabel.setText(record);
    }

    public  void setTopLabel(String text) {
        topLabel.setText(text);
    }

    public void setPotLabel(String text) {
        potLabel.setText(text);
    }

    public void setHighBetLabel(String text) {
        highBetLabel.setText(text);
    }

    public ArrayList<String> getPlayerNames() {
        return names;
    }

    public void resetStatRecords() {
        statRoundsPlayed.setText("" + 0);
        statHighestPot.setText("$0.0");
        statBestRecord.setText("0-0");
        writeStatsToFile();
    }

}
