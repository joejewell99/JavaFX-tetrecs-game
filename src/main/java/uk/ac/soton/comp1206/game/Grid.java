package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {
    private static final Logger logger = LogManager.getLogger(Grid.class);

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
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
     * Check whether a game piece can be placed in the grid at a given x,y
     * @param gamePiece the piece to play
     * @param placeX x location
     * @param placeY y location
     *
     * @return whether the piece can be played true or false
     */
    public boolean canPlayPiece(GamePiece gamePiece, int placeX, int placeY) {
        int[][] blocks = gamePiece.getBlocks();
        int topX = placeX - 1;
        int topY = placeY - 1;

        logger.info("checking if we can play a piece at a given x, y");
        for(int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                var blockValue = blocks[i][j];
                if(blockValue > 0) {
                    var gridValue = get(i + topX, j + topY);
                    if(gridValue != 0){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Play a piece by updating the grid with the piece blocks
     * @param piece the piece to be played
     * @param placeX the x position of where it will be placed
     * @param placeY the y position of where it will be placed
     */
    public void playPiece(GamePiece piece, int placeX, int placeY){
        logger.info("placing the piece at a given x, y");
          int colorValue = piece.getValue();
          int[][] blocks = piece.getBlocks();
          int topX = placeX - 1;
          int topY = placeY - 1;

          if(!canPlayPiece(piece, placeX, placeY)) return;

        for(int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks.length; j++) {
                //have the coordinate for a given block in the 3x3 blocks array
                var blockValue = blocks[i][j];
                if(blockValue > 0) {
                    set(topX + i, topY + j, colorValue);
                }
            }
        }

    }

    /**
     * Moves the object up.
     */
    public void moveObjectUp() {
        // Implementation goes here
    }

    /**
     * Moves the object down.
     */
    public void moveObjectDown() {
        // Implementation goes here
    }

    /**
     * Moves the object left.
     */
    public void moveObjectLeft() {
        // Implementation goes here
    }

    /**
     * Moves the object right.
     */
    public void moveObjectRight() {
        // Implementation goes here
    }

}
