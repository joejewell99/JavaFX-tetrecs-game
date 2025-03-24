package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.MultiMedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene implements NextPieceListener,RightClickedListener,LineClearedListener, GameLoopListener {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    /**
     * The instance of the Game class associated with this class.
     */
    protected Game game;

    private GameBoard board;
    private final MultiMedia challengeMusic = new MultiMedia();

    private final PieceBoard pieceBoard = new PieceBoard(3,3, 150, 150);
    private final PieceBoard smallerPieceBoard = new PieceBoard(3,3, 90, 90);
    private FadeTransition fadeTransition;

    private Rectangle timeBar = new Rectangle(800, 100, Color.GREEN);

    private Timeline colorTimeline;
    private ScaleTransition scaleTransition;




    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");

    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();
        this.colorTimeline = createColorTimeline();
        this.scaleTransition = createScaleTransition();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        challengeMusic.playBackgroundMusic("/music/game.wav");

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        mainPane.setMaxWidth(challengePane.getMaxWidth());
        mainPane.setMaxHeight(challengePane.getMaxHeight());
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        //Handle block on gameboard grid being clicked
        board.setGame(game);
        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClicked(this);


        for (int x = 0; x < game.getGrid().getCols(); x++) {
            for (int y = 0; y < game.getGrid().getRows(); y++) {
                GameBlock block = board.getBlock(x, y);
                int placeX = x;
                int placeY = y;
                int originalValue = block.getValue(); // Store the original value of the block
                block.setOnMouseEntered(event -> {
                    // Highlight the block on mouse enter
                    block.valueProperty().unbind();
                    block.setValue(1); // Set the value to the desired color index
                });

                block.setOnMouseExited(event -> {
                    // Revert the block to its original value on mouse exit
                    block.setValue(originalValue);
                    block.valueProperty().bind(game.getGrid().getGridProperty(placeX,placeY));
                });
            }
        }

        game.setNextPieceListener(this);
        game.setLineClearedListener(this);
        game.setOnGameLoop(this);

        //pieceboards UI
        pieceBoard.setTranslateY(-30);
        smallerPieceBoard.setTranslateY(-30);

        //Score
        Text score = new Text("Score");
        score.getStyleClass().add("score");
        Text scoreNumber = new Text();
        scoreNumber.textProperty().bind(game.scoreProperty().asString());
        scoreNumber.getStyleClass().add("score");

        //HighScore
        Text highScore = new Text("High Score");
        highScore.getStyleClass().add("hiscore");
        Text highScoreNumber = new Text();
        highScoreNumber.textProperty().bind(game.highScoreProperty().asString());
        highScoreNumber.getStyleClass().add("hiscore");

        VBox leftVerticalBox = new VBox(score, scoreNumber,highScore,highScoreNumber);
        mainPane.setLeft(leftVerticalBox);


        //level
        Text levelText = new Text("Level");
        levelText.getStyleClass().add("level");
        levelText.setTranslateY(-40);
        levelText.setScaleY(1.25);
        levelText.setScaleX(1.25);
        Text level = new Text();
        level.setScaleX(1.25);
        level.setScaleY(1.25);
        level.setTranslateY(-40);
        level.textProperty().bind(game.levelProperty().asString());
        level.getStyleClass().add("level");


        //lives
        Text livesText = new Text("Lives");
        livesText.getStyleClass().add("lives");
        livesText.setTranslateY(-30);
        Text lives = new Text();
        lives.textProperty().bind(game.livesProperty().asString());
        lives.getStyleClass().add("lives");
        lives.setTranslateY(-40);

        //multi
        Text multiplierText = new Text("Multiplier");
        multiplierText.getStyleClass().add("lives");
        multiplierText.setScaleY(.75);
        multiplierText.setScaleX(.75);
        multiplierText.setTranslateY(-10);
        Text multiplier = new Text();
        multiplier.setTranslateY(-25);
        multiplier.textProperty().bind(game.multiplierProperty().asString());
        multiplier.getStyleClass().add("lives");

        //right side component
        VBox rightVerticalBox = new VBox(multiplierText,multiplier,livesText,lives,levelText,level, pieceBoard, smallerPieceBoard);
        rightVerticalBox.setAlignment(Pos.CENTER);
        rightVerticalBox.setSpacing(10);
        rightVerticalBox.setMaxWidth(mainPane.getMaxWidth());
        rightVerticalBox.setMaxHeight(mainPane.getMaxHeight());

        mainPane.setRight(rightVerticalBox);
        mainPane.setFocusTraversable(true);

        colorTimeline.play();
        scaleTransition.play();

        mainPane.setBottom(timeBar);

    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        if(game.getGrid().canPlayPiece(game.getCurrentPiece(),gameBlock.getX(),gameBlock.getY())) {
            resetTimerBarAnimation();
        }
        game.blockClicked(gameBlock);
    }




    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    logger.info("ESC key pressed");
                    shutdownGame();
                    break;
                case R:
                case SPACE:
                    swapCurrentPieces();
                    break;
                case UP :
                case W:
                    game.getGrid().
                    moveObjectUp();
                    break;
                case DOWN:
                case S:
                    game.getGrid().
                    moveObjectDown();
                    break;
                case LEFT:
                case A:
                    game.getGrid().
                    moveObjectLeft();
                    break;
                case D:
                case RIGHT:
                    game.getGrid().
                    moveObjectRight();
                    break;
                case Q, OPEN_BRACKET, Z:
                    game.rotateCurrentPieceLeft();
                    challengeMusic.playAudioFile("/sounds/rotate.wav");
                    pieceBoard.setNextPiece(game.getCurrentPiece());
                    break;
                case E, CLOSE_BRACKET, C:
                    game.rotateCurrentPiece();
                    challengeMusic.playAudioFile("/sounds/rotate.wav");
                    pieceBoard.setNextPiece(game.getCurrentPiece());
                    break;
                default:
                    // Handle other key presses if needed
                    break;
            }
        });


        pieceBoard.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY ) {
                game.rotateCurrentPiece();
                challengeMusic.playAudioFile("/sounds/rotate.wav");
                pieceBoard.setNextPiece(game.getCurrentPiece());
            }
        });



    }
    /**
     * Shuts down the game by stopping background music, canceling the game timer, and returning to the main menu.
     */
    public void shutdownGame() {
        challengeMusic.stopBackgroundMusic();
        game.getGameTimer().cancel();
        gameWindow.startMenu();
    }
    /**
     * Initiates the transition to the score screen, stopping background music in the process.
     */
    @Override
    public void ScoreScreen() {
        challengeMusic.stopBackgroundMusic();
        gameWindow.startScorePage(game);
    }
    /**
     * Sets the next game piece and following piece on the corresponding piece boards.
     *
     * @param piece The next game piece to be set.
     * @param followingPiece The following game piece to be set.
     */
    public void nextPiece(GamePiece piece, GamePiece followingPiece) {
        pieceBoard.setNextPiece(piece);
        smallerPieceBoard.setNextPiece(followingPiece);
    }
    /**
     * Swaps the current game piece with the following game piece, playing a sound effect in the process.
     */
    public void swapCurrentPieces(){
        GamePiece mediator = game.getCurrentPiece();
        game.setCurrentPiece(game.getFollowingPiece());
        game.setFollowingPiece(mediator);
        GamePiece bigPeace = game.getCurrentPiece();
        GamePiece littePiece = game.getFollowingPiece();
        challengeMusic.playAudioFile("/sounds/rotate.wav");
        this.nextPiece(bigPeace,littePiece);


    }
    /**
     * Handles the event when a right-click occurs on the game board. Rotates the current piece, plays a rotation sound,
     * and updates the next piece displayed on the piece board.
     *
     * @param event The MouseEvent representing the right-click event.
     */
    @Override
    public void onRightClicked(MouseEvent event) {
        logger.info("Right clicked on GameBoard");
        if (event.getButton() == MouseButton.SECONDARY) {
            game.rotateCurrentPiece();
            challengeMusic.playAudioFile("/sounds/rotate.wav");
            pieceBoard.setNextPiece(game.getCurrentPiece());

        }
    }
    /**
     * Handles the event when lines are cleared in the game. Logs the event and initiates a fade-out animation for the
     * cleared blocks on the game board.
     *
     * @param coordinates The set of coordinates representing the blocks that were cleared.
     */
    @Override
    public void onLineCleared(Set<GameBlockCoordinate> coordinates) {
        if(game.getNumberOfLines() > 0){
            logger.info(" Lines have been cleared");
            board.fadeOut((HashSet<GameBlockCoordinate>) coordinates);
        }else{
            logger.info("No lines have been cleared");
        }


    }
    /**
     * The game loop logic executed in each iteration of the game loop. Decreases the player's lives, advances to the next
     * game piece, resets the multiplier, triggers a timer bar animation reset, and plays a sound effect indicating the
     * player lost a life.
     */
    @Override
    public void gameLoop(){
        game.setLives(game.getLives() - 1);
        game.nextPiece();
        game.setMultiplier(1);
        resetTimerBarAnimation();
        challengeMusic.playAudioFile("/sounds/lostlife.wav");
    }
    /**
     * Resets the animation of the timer bar, restarting both the color timeline and scale transition. The scale transition
     * simulates the time bar decreasing over time.
     */
    private void resetTimerBarAnimation() {
        // Stop and restart the color timeline
        colorTimeline.stop();
        colorTimeline.play();

        // Stop and restart the scale transition
        scaleTransition.stop();
        scaleTransition.setToX(1);
        scaleTransition.setDuration(Duration.millis(1));
        scaleTransition.play();
        scaleTransition.setOnFinished(actionEvent -> {
            scaleTransition.setDuration(Duration.millis(game.getTimerDelay()));
            scaleTransition.setToX(0);
            scaleTransition.play();
        });


    }
    /**
     * Creates a timeline for animating the color of the time bar from green to red over a specified duration.
     * @return The created Timeline object.
     */
    private Timeline createColorTimeline() {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(timeBar.fillProperty(), Color.GREEN)),
                new KeyFrame(Duration.millis(game.getTimerDelay()), new KeyValue(timeBar.fillProperty(), Color.RED))
        );
    }
    /**
     * Creates a scale transition for animating the width of the time bar to simulate time running out.
     * @return The created ScaleTransition object.
     */
    private ScaleTransition createScaleTransition() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(game.getTimerDelay()), timeBar);
        scaleTransition.setToX(0);
        return scaleTransition;
    }

}
