package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game{

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Property representing the high score in the game.
     */
    private final IntegerProperty highScore = new SimpleIntegerProperty(getHighScore());

    /**
     * Property representing the current score in the game.
     */
    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Property representing the current level in the game.
     */
    private final IntegerProperty level = new SimpleIntegerProperty(0);

    /**
     * Property representing the number of lives remaining in the game.
     */
    private final IntegerProperty lives = new SimpleIntegerProperty(3);

    /**
     * Property representing the current score multiplier in the game.
     */
    private final IntegerProperty multiplier = new SimpleIntegerProperty(1);


    private int numberOfLines = 0;
    private int numberOfBlocks = 0;

    private NextPieceListener nextPieceListener;
    private LineClearedListener lineClearedListener;
    private GameLoopListener gameLoopListener;

    private GamePiece currentPiece;
    private GamePiece followingPiece;

    private final MultiMedia gameMedia = new MultiMedia();

    private Timer gameTimer;

    /**
     * The number of rows in the grid.
     */
    protected final int rows;

    /**
     * The number of columns in the grid.
     */
    protected final int cols;


    private Random random = new Random();

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        followingPiece = spawnPiece();

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        nextPiece();
        gameTimer = new Timer();
        runTimer();


    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        if(grid.canPlayPiece(currentPiece,x,y)){
            grid.playPiece(currentPiece,x,y);
            gameMedia.playAudioFile("/sounds/place.wav");
            nextPiece();
            gameTimer.cancel();
            gameTimer = new Timer();
            runTimer();
        }else{
            gameMedia.playAudioFile("/sounds/error.mp3");
        }
        afterPiece();
        score(getNumberOfLines(),getNumberOfBlocks());
    }

    /**
     * Spawns a GamePiece
     * @return returns a new GamePiece object with a random color of value between 0-15
     */
    public GamePiece spawnPiece() {
        int randomNumber = random.nextInt(GamePiece.PIECES);
        var randomPiece = GamePiece.createPiece(randomNumber);
        logger.info("Picking random piece: {}", randomPiece);
        return randomPiece;
    }

    /**
     * generates the next piece to be placed
     * generates a future piece to be placed too
     */
    public void nextPiece(){
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        logger.info("The next Piece is: {}",currentPiece);
        if(nextPieceListener != null) {
            nextPieceListener.nextPiece(currentPiece, followingPiece);
        }
    }

    /**
     * After a piece has been played on the board
     * works out which blocks form a line and need to be cleared at which coordinates
     * invokes the linesClearedListener method to clear those lines/blocks from the board
     */
    public void afterPiece() {
        HashSet<GameBlockCoordinate> blocksToClearCoordinates = new HashSet<>();
        for (int x = 0; x < grid.getCols(); x++) {
            int xBlockCounter = 0;
            for (int y = 0; y < grid.getRows(); y++) {
                if(grid.get(x, y) != 0) {
                    xBlockCounter++;
                }
            }

            if (xBlockCounter == grid.getRows()) {
                numberOfLines++;
                for (int y = 0; y < grid.getRows(); y++) {
                    blocksToClearCoordinates.add(new GameBlockCoordinate(x, y));
                }
            }
        }

        for (int y = 0; y < grid.getRows(); y++) {
            int yBlockCounter = 0;
            for (int x = 0; x < grid.getCols(); x++) {
                if(grid.get(x, y) != 0) {
                    yBlockCounter++;
                }
            }
            if (yBlockCounter == grid.getCols()) {
                numberOfLines++;
                for (int x = 0; x < grid.getCols(); x++) {
                    blocksToClearCoordinates.add(new GameBlockCoordinate(x, y));
                }
            }
        }

        for (GameBlockCoordinate coordinate: blocksToClearCoordinates){
            grid.set(coordinate.getX(), coordinate.getY(), 0);
            numberOfBlocks++;
        }
        if(lineClearedListener != null ) {
            lineClearedListener.onLineCleared(blocksToClearCoordinates);
        }

    }


    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * sets the current piece in game
     * @param currentPiece GamePiece to be set
     */
    public void setCurrentPiece(GamePiece currentPiece) {
        this.currentPiece = currentPiece;
    }

    /**
     * gets the current score in game
     * @return value held in score property
     */
    public int getScore() {
        return score.get();
    }

    /**
     * sets the score to the integer parameter
     * @param score score to be set
     */
    public void setScore(int score) {
        this.score.set(score);
    }
    /**
     * Returns the IntegerProperty object representing the score property.
     * @return The IntegerProperty object representing the score property.
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Gets the value of the level property.
     * @return The value of the level property.
     */
    public int getLevel() {
        return level.get();
    }
    /**
     * Sets the value of the level property.
     * @param level The new value for the level property.
     */
    public void setLevel(int level) {
        this.level.set(level);
    }

    /**
     * Gets the IntegerProperty object representing the level property.
     * @return The IntegerProperty object representing the level property.
     */
    public IntegerProperty levelProperty() {
        return level;
    }

    /**
     * Gets the value of the lives property.
     * @return The value of the lives property.
     */
    public int getLives() {
        return lives.get();
    }

    /**
     * Sets the value of the lives property.
     * @param lives The new value for the lives property.
     */
    public void setLives(int lives) {
        this.lives.set(lives);
    }

    /**
     * Gets the IntegerProperty object representing the lives property.
     * @return The IntegerProperty object representing the lives property.
     */
    public IntegerProperty livesProperty() {
        return lives;
    }

    /**
     * Gets the value of the multiplier property.
     * @return The value of the multiplier property.
     */
    public int getMultiplier() {
        return multiplier.get();
    }

    /**
     * Sets the value of the multiplier property.
     * @param multiplier The new value for the multiplier property.
     */
    public void setMultiplier(int multiplier) {
        this.multiplier.set(multiplier);
    }

    /**
     * Gets the IntegerProperty object representing the multiplier property.
     * @return The IntegerProperty object representing the multiplier property.
     */
    public IntegerProperty multiplierProperty() {
        return multiplier;
    }

    /**
     * if a line has been cleared
     * Uses mathematical equations to calculate the score needed to be added to the current score
     * handles the multiplier value after a piece has been played
     * increases the level by 1 for every 1000 score scored
     * else, multiplier set to 1 and no score added.
     * @param numberOfLines number of lines removed
     * @param numberOfBlocks number of blocks removed
     */
    public void score(int numberOfLines, int numberOfBlocks) {
        if(numberOfLines > 0) {
            int scoreToAdd = numberOfLines * numberOfBlocks * 10 * getMultiplier();
            setScore(getScore() + scoreToAdd);
            setMultiplier(getMultiplier() + 1);
            int fixedLevel = (int) Math.floor((double) getScore() /1000);
            setLevel(fixedLevel);
            gameMedia.playAudioFile("/sounds/clear.wav");


        }else{
            setMultiplier(1);
        }
        setNumberOfLines(0);
        setNumberOfBlocks(0);

    }
    /**
     * gets the number of lines.
     * @return The number of lines in the game.
     */
    public int getNumberOfLines() {
        return numberOfLines;
    }

    /**
     * gets the number of blocks.
     * @return The number of blocks in the game.
     */
    public int getNumberOfBlocks() {
        return numberOfBlocks;
    }

    /**
     * Setter for the number of lines.
     * @param numberOfLines The new number of lines in the game.
     */
    public void setNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
    }

    /**
     * Setter for the number of blocks.
     * @param numberOfBlocks The new number of blocks in the game.
     */
    public void setNumberOfBlocks(int numberOfBlocks) {
        this.numberOfBlocks = numberOfBlocks;
    }

    /**
     * Setter for the listener when receiving the next game piece.
     * @param nextPieceListener The listener for receiving the next game piece.
     */
    public void setNextPieceListener(NextPieceListener nextPieceListener) {
        this.nextPieceListener = nextPieceListener;
    }

    /**
     * gets the current game piece.
     * @return The current game piece.
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Rotates current piece right
     */
    public void rotateCurrentPiece(){
        currentPiece.rotate();
    }

    /**
     * Rotates current piece left.
     */
    public void rotateCurrentPieceLeft(){
        currentPiece.rotateLeft();
    }

    /**
     * gets the following piece eg.(piece after current piece)
      * @return following GamePiece.
     */
    public GamePiece getFollowingPiece() {
        return followingPiece;
    }


    /**
     * Sets the following game piece, which is the piece that will appear next in the game.
     * @param followingPiece The game piece that will appear next in the game.
     */
    public void setFollowingPiece(GamePiece followingPiece) {
        this.followingPiece = followingPiece;
    }

    /**
     * Sets the listener for handling events when lines are cleared in the game.
     * @param lineClearedListener The listener for handling events when lines are cleared.
     */
    public void setLineClearedListener(LineClearedListener lineClearedListener) {
        this.lineClearedListener = lineClearedListener;
    }

    /**
     * Sets the listener for the game loop, which will be called during each iteration of the game loop.
     * @param listener The listener for the game loop.
     */
    public void setOnGameLoop(GameLoopListener listener) {
        // Set the game loop listener
        this.gameLoopListener = listener;
    }

    /**
     * gets the time delay between game loop
     * ensures it doesn't go below 2500 milliseconds
     * @return time delay between each game loop.
     */
    public int getTimerDelay(){
        int timeDelay;
        timeDelay = 12000 - (500 * getLevel());
        if(timeDelay < 2500) {
            timeDelay= 2500;
        }
        return timeDelay;
    }
    /**
     * Runs a timer task at a fixed rate, which executes the game loop listener's gameLoop method periodically.
     * The timer task is scheduled based on the timer delay set by getTimerDelay method.
     * If the number of lives reaches zero, the timer is canceled and the game loop listener's ScoreScreen method is called.
     */
    public void runTimer() {
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (getLives() <= 0) {
                        gameTimer.cancel();
                        gameLoopListener.ScoreScreen();
                    } else {
                        gameLoopListener.gameLoop();
                    }
                });
            }
        }, getTimerDelay(), getTimerDelay());

    }
    /**
     * gets the high score from the scores.txt file.
     * If the file doesn't exist or is empty, returns 0.
     * Which is determined by reading the first line
     * @return the high score read from the scores.txt file
     */
    public int getHighScore() {

        try {
            BufferedReader fileReader = new BufferedReader(new FileReader("scores.txt"));
            String firstLine = fileReader.readLine();
            if (firstLine == null){
                return 0;
            }

            BufferedReader comparisonReader = new BufferedReader(new FileReader("scores.txt"));
            String line;
            int highScore = 0;
            int scoreComparison;
            while ((line = comparisonReader.readLine()) != null){
                String[] parts = line.split(":");
                String name = parts[0];
                scoreComparison = Integer.parseInt(parts[1]);
                highScore = Math.max(scoreComparison, highScore);

            }
            fileReader.close();
            comparisonReader.close();
            return highScore;

        } catch (FileNotFoundException e) {
            return 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * gets the property object representing the high score.
     * @return the IntegerProperty object representing the high score
     */
    public IntegerProperty highScoreProperty() {
        return highScore;
    }
    /**
     * gets the game timer.
     * @return a Timer object named gameTimer
     */
    public Timer getGameTimer() {
        return gameTimer;
    }




}
