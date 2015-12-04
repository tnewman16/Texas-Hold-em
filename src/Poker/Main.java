package Poker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("poker.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Poker, I hardly know her!");
        primaryStage.setScene(new Scene(root, 1000, 1000));
        primaryStage.show();
        Controller controller = loader.getController();


        String stats = "";

        // Obtain previously recorded stats and display them
        File file = new File("poker_stats.txt");

        if (!file.exists()) {   // if the file does not already exist, create it
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            PrintStream printer = new PrintStream(out);

            // Create new stats
            printer.println("0");       // Rounds Played
            printer.println("0.0");     // Highest Bet
            printer.println("0-0");     // Best Record
            stats = "0 0.0 0-0";

        } else {    // otherwise read the file to obtain stat values
            try {
                FileReader fr = new FileReader("poker_stats.txt");
                BufferedReader reader = new BufferedReader(fr);

                for (int i=0; i<3; i++) {
                    stats += reader.readLine() + " ";
                }

                reader.close();

            } catch (IOException e) {
                System.err.println("Could not read stats file...");
            }

        }

        controller.setStatInfo(stats);
        controller.setPotLabel("$0");
        controller.setHighBetLabel("$0");
        controller.disarmCommands();
        controller.disarmFlipButton();
        controller.setLiveInput("Welcome to Poker! Enter the names of each player one at a time. Click play when ready.");

    }


    public static void main(String[] args) {
        launch(args);
    }
}
