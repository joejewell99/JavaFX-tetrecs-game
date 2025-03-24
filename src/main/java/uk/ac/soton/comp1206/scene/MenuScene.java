package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.MultiMedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    MultiMedia menuMedia;

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        menuMedia = new MultiMedia();
        menuMedia.playBackgroundMusic("/music/menu.mp3");

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title + animations + GUI + Sound
        Image titleImage = new Image("C:\\Comp1206\\coursework\\src\\main\\resources\\images\\TetrECS.png");
        ImageView title = new ImageView(titleImage);
        title.setFitHeight(40);
        title.setFitWidth(180);
        mainPane.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        title.setTranslateY(100);
        //Translate
        TranslateTransition titleTransition = new TranslateTransition(Duration.millis(2000),title);
        titleTransition.setByY(100);
        titleTransition.setCycleCount(1);
        titleTransition.play();
        //Scale
        ScaleTransition scaleTransitionTitle = new ScaleTransition(Duration.millis(1000),title);
        scaleTransitionTitle.setByX(2);
        scaleTransitionTitle.setByY(2);
        scaleTransitionTitle.play();
        //Fade
        FadeTransition titleFadeTransition =new FadeTransition(Duration.millis(1000), title);
        titleFadeTransition.setFromValue(0);
        titleFadeTransition.setToValue(1);
        titleFadeTransition.play();

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        //play button
        var button = new Button("Play");
        button.getStyleClass().add("menuItem");

        //Bind the button action to the startGame method in the menu
        button.setOnAction(this::startGame);

        //Multiplayer button
        Button multiplayerButton = new Button("Multiplayer");
        multiplayerButton.getStyleClass().add("menuItem");
        multiplayerButton.setOnAction(actionEvent -> {
            multiplayerButton.setVisible(false);
            multiplayerButton.setDisable(true);
        });
        //How to play button
        Button hTPButton = new Button("How to play");
        hTPButton.getStyleClass().add("menuItem");
        //Exit button
        Button exitButton = new Button("Exit");
        exitButton.getStyleClass().add("menuItem");

        //Center Vertical box
        VBox buttonBox = new VBox();
        buttonBox.setAlignment(Pos.CENTER); // Center align the buttons vertically
        buttonBox.setPadding(new Insets(250,0,0,0));
        buttonBox.setSpacing(10); // Add spacing between buttons

        buttonBox.getChildren().addAll(button, multiplayerButton, hTPButton, exitButton);

        mainPane.setCenter(buttonBox);

        hTPButton.setOnAction(this::startInstructionsPage);

        exitButton.setOnAction(actionEvent -> System.exit(0));
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                logger.info("ESC key pressed");
                System.exit(0);
            }
        });

    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        menuMedia.stopBackgroundMusic();
        gameWindow.startChallenge();
    }
    /**
     * Initiates the transition to the instructions page of the game window.
     * @param event The ActionEvent triggering the method call.
     */
    private void startInstructionsPage(ActionEvent event) {
        gameWindow.startInstructionsPage();
    }

}
