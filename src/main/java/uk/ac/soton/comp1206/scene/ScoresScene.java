package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiMedia;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.*;
/**
 * Represents a scene for displaying scores in the game.
 * This scene extends the {@link BaseScene} class to inherit basic scene functionalities
 * and implements the {@link CommunicationsListener} interface to handle communication events.
 */
public class ScoresScene extends BaseScene implements CommunicationsListener{
    private static final Logger logger = LogManager.getLogger(InstructionScene.class);
    MultiMedia scoresMedia = new MultiMedia();

    private final Game endOfGame;
    private final List<Pair<String, Integer>> remoteScoreList = new ArrayList<>();
    private final List<Pair<String, Integer>> scorePairList = new ArrayList<>();
    private BorderPane mainPane;
   // private final Communicator communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9700");
    private final CommunicationsListener communicationsListener = this;
    private final VBox onlineScores = new VBox();
    private final AnchorPane anchorPane = new AnchorPane();

    /**
     * Constructs a new ScoresScene object with the specified game window and game state.
     * @param gameWindow     The game window associated with this scene.
     * @param currentGameState The current state of the game.
     */
    public ScoresScene(GameWindow gameWindow, Game currentGameState) {
        super(gameWindow);
        this.endOfGame = currentGameState;
        //communicator.addListener(this::receiveCommunication);
    }
    /**
     * Initializes the scene.
     */
    @Override
    public void initialise() {

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                logger.info("ESC key pressed");
                shutdownResults();
            }
        });
    }
    /**
     * Builds the ScoreScene, including a brief pause using Thread.sleep(10) to allow the scene to fully initialize.
     */
    @Override
    public void build() {

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scoresMedia.playAudioFile("/music/end.wav");

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());
        var scoresPane = new StackPane();
        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        Image backgroundImage = new Image("C:\\Comp1206\\coursework\\src\\main\\resources\\images\\5.jpg");
        // Create a BackgroundImage
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        scoresPane.setBackground(new Background(background));
        root.getChildren().add(scoresPane);

        mainPane = new BorderPane();
        scoresPane.getChildren().add(mainPane);
        mainPane.setCenter(anchorPane);

        //title
        Image titleImage = new Image("C:\\Comp1206\\coursework\\src\\main\\resources\\images\\TetrECS.png");
        ImageView title = new ImageView(titleImage);
        title.setFitHeight(70);
        title.setFitWidth(300);
        mainPane.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        //online scores
        BorderPane.setAlignment(onlineScores, Pos.TOP_LEFT);
        onlineScores.setMaxWidth(100); // Set maximum width to the maximum possible value
        onlineScores.setMaxHeight(200);
        onlineScores.setTranslateX(-100);
        onlineScores.setTranslateY(1);
        mainPane.setRight(onlineScores);

        int usersScore = endOfGame.getScore();
        Label promptLabel = new Label("Please enter your name:");
        promptLabel.getStyleClass().add("menuItem");
        TextField nameField = new TextField();
        nameField.getStyleClass().add("TextField");
        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("menuItem:hover");
        Button menuButton = new Button("Exit");
        menuButton.getStyleClass().add("menuItem:hover");
        VBox bottomVBox = new VBox(promptLabel,nameField, submitButton,menuButton);
        mainPane.setBottom(bottomVBox);

        menuButton.setOnAction(actionEvent -> gameWindow.startMenu());

        submitButton.setOnAction(actionEvent -> {
            submitButton.setVisible(false);
            submitButton.setDisable(true);

            String name = nameField.getText();
            scorePairList.add(new Pair<>(name,usersScore));
            writeScores(scorePairList);
            loadScores("scores.txt");
            mainPane.setBottom(null);

        });

    }
    /**
     * Reveals a Text node by fading it in over a specified duration.
     * @param text the Text node to reveal
     */
    public void reveal(Text text) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(4),text);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();


    }
    /**
     * Loads scores from a file and displays them in a VBox within an AnchorPane.
     * @param scoresFile the file containing the scores to load
     */
    public void loadScores(String scoresFile) {
        try {
            Text localScoreText = new Text("Local Scores");
            localScoreText.getStyleClass().add("scorelist");
            VBox vBox = new VBox(localScoreText);
            vBox.setAlignment(Pos.TOP_CENTER);


            anchorPane.getChildren().add(vBox);
            AnchorPane.setTopAnchor(vBox, 0.0); // Set top anchor to 0
            AnchorPane.setBottomAnchor(vBox, 0.0); // Set bottom anchor to 0
            AnchorPane.setLeftAnchor(vBox, 0.0); // Set left anchor to 0
            AnchorPane.setRightAnchor(vBox, 0.0); // Set right anchor to 0

            scorePairList.clear();
            BufferedReader scoresReader = new BufferedReader(new FileReader(scoresFile));
            String line;
            while ((line = scoresReader.readLine()) != null) {
                String[] parts = line.split(":");
                String name = parts[0];
                int score = Integer.parseInt(parts[1]);
                scorePairList.add(new Pair<>(name, score));
            }
            scorePairList.sort((p1, p2) -> p2.getValue().compareTo(p1.getValue()));
            for(Pair<String,Integer> pair:scorePairList){
                String name = pair.getKey();
                int score = pair.getValue();
                Text pairText = new Text(name + ":" + score);
                pairText.getStyleClass().add("channelItem");
                vBox.getChildren().add(pairText);
                reveal(pairText);
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * Writes the provided score list to a file.
     * @param scoreList the list of scores to write
     */
    public void writeScores(List<Pair<String, Integer>> scoreList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))){
            for (Pair<String, Integer> pair : scoreList) {
                writer.write(pair.getKey() + ":" + pair.getValue()+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Shuts down the results display and returns to the main menu.
     */
    public void shutdownResults() {
        scene = null;
        gameWindow.startMenu();
    }
    /**
     * Loads online scores by sending a request to the server.
     */

    /**
    public void loadOnlineScores() {
        communicator.send("HISCORES");
    }
     */

    /**
     * Writes an online score to the server.
     * @param name the name associated with the score
     * @param score the score to write
     */

    /**
    public void writeOnlineScore(String name, int score) {
        communicator.send("HISCORE " + name + ":" + score);
    }
    */

    /**
     * Receives communication responses from the server and processes them, updating the UI with
     * online scores if applicable.
     * @param response the response received from the server
     */
    @Override
    public void receiveCommunication(String response) {
        logger.info("Response received!");
        if (response.startsWith("NEWSCORE") || response.startsWith("HISCORES")) {
            logger.info("Response received!");

            String[] lines = response.substring(9).split("\n");
            for (String line : lines) {
                String[] parts = line.split(":");
                String name = parts[0];
                int score;
                try {
                    score = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    score = 0; // Handle non-integer scores by setting them to zero
                }
                remoteScoreList.add(new Pair<>(name, score));
            }
            logger.info(remoteScoreList);

            Text onlineScoreText = new Text("Online Scores");
            onlineScoreText.getStyleClass().add("scorelist");
            onlineScores.getChildren().add(onlineScoreText);
            for(Pair<String,Integer> pair:remoteScoreList){
                String name = pair.getKey();
                int score = pair.getValue();
                Text pairText = new Text(name + ":" + score);
                pairText.getStyleClass().add("channelItem");
                onlineScores.getChildren().add(pairText);
                reveal(pairText);
            }
            logger.info(onlineScores.toString());
        }
    }
}
