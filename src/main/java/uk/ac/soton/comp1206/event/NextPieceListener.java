package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
/**
 * The NextPiece Listener is used for listening for when a next piece is needed to be generated.
 */
public interface NextPieceListener {
    /**
     * Handles Piece generation for the 2 Piece boards
     * @param nextGamePiece this will be set to the following piece
     * @param followingGamePiece this will spawn a new piece to display of the smallerPieceBoard
     */
    public void nextPiece(GamePiece nextGamePiece, GamePiece followingGamePiece);
}
