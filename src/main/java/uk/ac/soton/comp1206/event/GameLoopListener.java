package uk.ac.soton.comp1206.event;
/**
 * The GameLoop Listener is used for listening for the event that the game needs to loop/
 * a life has been lost/ player was unable to place a piece in time .
 */
public interface GameLoopListener {
     /**
      * Handles the game state when a player fails to place a piece in time
      */
     public void gameLoop();


     /**
      * Shuts down the game gracefully, releasing any resources and performing necessary cleanup tasks.
      */
     void shutdownGame();

     /**
      * listens or is invoked when a player loses all their lives/ end of game
      */
     void ScoreScreen();
}
