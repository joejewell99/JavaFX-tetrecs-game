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
    private static double masterVolume = 1.0;
    /**
     * Plays the specified audio file as foreground sound.
     * @param audioFile the path to the audio file to be played
     */
    public void playAudioFile(String audioFile){
          foregroundSound = new MediaPlayer(new Media(getClass().getResource(audioFile).toExternalForm()));
          foregroundSound.setVolume(masterVolume);
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
        backgroundMusic.setVolume(masterVolume);
        backgroundMusic.play();

    }

    /**
     * Sets the master volume between 0.0 and 1.0.
     * @param volume the desired master volume
     */
    public void setMasterVolume(double volume) {
        masterVolume = Math.max(0.0, Math.min(1.0, volume));
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(masterVolume);
        }
        if (foregroundSound != null) {
            foregroundSound.setVolume(masterVolume);
        }
    }

    /**
     * Gets the current master volume.
     * @return master volume between 0.0 and 1.0
     */
    public double getMasterVolume() {
        return masterVolume;
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
