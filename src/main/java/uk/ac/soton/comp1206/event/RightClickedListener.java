package uk.ac.soton.comp1206.event;

import javafx.event.Event;
import javafx.scene.input.MouseEvent;
/**
 * The Communications Listener is used for listening to messages received by the communicator.
 */
public interface RightClickedListener {
    /**
     * Handles a Right Click event
     * @param event Mouse event click on screen
     */
    public void onRightClicked(MouseEvent event);
}
