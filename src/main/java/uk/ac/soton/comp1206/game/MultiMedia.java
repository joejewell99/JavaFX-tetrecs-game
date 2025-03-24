package uk.ac.soton.comp1206.game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
/**
 * Represents a multimedia utility class.
 * This class provides functionalities related to multimedia operations.
 */
public class MultiMedia {
    private MediaPlayer foregroundSound;
    private MediaPlayer backgroundMusic;
    /**
     * Plays the specified audio file as foreground sound.
     * @param audioFile the path to the audio file to be played
     */
    public void playAudioFile(String audioFile){
          foregroundSound = new MediaPlayer(new Media(getClass().getResource(audioFile).toExternalForm()));
          foregroundSound.play();
    }
    /**
     * Plays the specified music file as background music.
     * The background music will loop indefinitely until stopped.
     * @param musicFile the path to the music file to be played as background music
     */
    public void playBackgroundMusic(String musicFile){
        backgroundMusic = new MediaPlayer(new Media(getClass().getResource(musicFile).toExternalForm()));
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.play();

    }
    /**
     * Stops the background music if it is currently playing.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
}
