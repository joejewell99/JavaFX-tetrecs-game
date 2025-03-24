package uk.ac.soton.comp1206.component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
/**
 * Represents a board for managing pieces in a game.
 * This class extends the {@link GameBoard} class, inheriting its functionalities
 * and providing additional features specific to managing pieces.
 */
public class PieceBoard extends GameBoard{
    /**
     * Constructor for a PieceBoard object
     * @param cols number of columns the board will have
     * @param rows number of rows the board will have
     * @param width width of the PieceBoard
     * @param height height of the PieceBoard
     */
    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
    }

    /**
     * loops through the rows and columns of the GamePiece parsed inand uses the coordinates
     * for each GameBlock of the GamePiece and sets the block on the grid at that given coordinate
     * to the correct colour value which generates an image for the Piece parsed in.
     * @param piece this is the GamePiece which will be shown on the PieceBoard
     */
    public void setNextPiece(GamePiece piece) {

        clearBoard(); // Clear the board before setting the new piece
        if (piece != null) {
            // Place the upcoming piece on the board
            int[][] shape = piece.getBlocks();
            for (int x = 0; x < grid.getCols(); x++) {
                for (int y = 0; y < grid.getRows(); y++) {
                    if (shape[x][y] != 0) {
                        super.grid.set(x, y, piece.getValue()); // Set cell with appropriate color
                    }
                }
            }
        }
    }

    /**
     * loops through the grid of the PieceBoard and sets each block to a colour value of 0;
     * making it transparent and essentially clearing the PieceBoard.
     */
    public void clearBoard() {
        for (int i = 0; i < grid.getCols(); i++) {
            for (int j = 0; j < grid.getRows(); j++) {
                super.grid.set(i, j, 0); // Set cell color to transparent
            }
        }
    }

}
