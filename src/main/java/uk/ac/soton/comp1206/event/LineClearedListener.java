package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.Set;
/**
 * The LineCleared Listener is used for listening for the event that lines have been cleared
 */
public interface LineClearedListener {
    /**
     * handles the event of which a line has been cleared
     * @param coordinates the set of block coordinates needed to be cleared.
     */
    void onLineCleared(Set<GameBlockCoordinate> coordinates);
}
