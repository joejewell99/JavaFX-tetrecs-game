package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Key;
import java.util.ArrayList;
/**
 * Represents an instruction scene in the game.
 * This scene provides instructions or guidelines to the player.
 * It extends the {@link BaseScene} class, which provides basic functionality for scenes in the game.
 */
public class InstructionScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(InstructionScene.class);

    private ArrayList<PieceBoard> allPieces = new ArrayList<>();

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instruction Scene");
    }

    @Override
    public void initialise() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                logger.info("ESC key pressed");
                this.goToStartMenu();
            }
        });
    }

    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var instructionPane = new StackPane();
        instructionPane.setMaxWidth(gameWindow.getWidth());
        instructionPane.setMaxHeight(gameWindow.getHeight());
        instructionPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructionPane);

        var mainPane = new BorderPane();
        instructionPane.getChildren().add(mainPane);

        Image instructionsImage = new Image("C:\\Comp1206\\coursework\\src\\main\\resources\\images\\instructions.png");
        ImageView imageView = new ImageView(instructionsImage);
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(gameWindow.getScene().widthProperty());
        imageView.fitHeightProperty().bind(gameWindow.getScene().heightProperty());
        mainPane.setTop(imageView);
        mainPane.setFocusTraversable(true);

        //GamePiece text title
        Text gamePieceText = new Text("Game Pieces");
        gamePieceText.getStyleClass().add("bigtitle");
        mainPane.setCenter(gamePieceText);
        gamePieceText.setTranslateY(-25);
        HBox piecesDisplay = new HBox();
        piecesDisplay.setSpacing(2);
        piecesDisplay.setTranslateY(-30);

        for(int i = 0; i < GamePiece.PIECES; i++){
            GamePiece currentPiece = GamePiece.createPiece(i);
            PieceBoard gridForPiece = new PieceBoard(3,3,50,50);
            gridForPiece.setNextPiece(currentPiece);
            piecesDisplay.getChildren().add(gridForPiece);

        }

        mainPane.setBottom(piecesDisplay);
    }

    private void goToStartMenu() {
        gameWindow.startMenu();
    }
}

