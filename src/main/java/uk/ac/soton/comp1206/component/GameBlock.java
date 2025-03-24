package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE,
    };


    /**
     * GameBoard object
     */
    private final GameBoard gameBoard;

    /**
     * Width and height of the current block.
     */
    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;
    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Integer property for the colour of the block
     *
     * @return Integer property (value)
     */
    public IntegerProperty valueProperty() {
        return value;
    }

    /**
     * setter for the colour value between 1-15
     *
     * @param value int value 1-15 representing the colour of the block
     */
    public void setValue(int value) {
        this.value.set(value);
    }

    /**
     * Create a new single Game Block
     *
     * @param gameBoard the board this block belongs to
     * @param x         the column the block exists in
     * @param y         the row the block exists in
     * @param width     the width of the canvas to render
     * @param height    the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     *
     * @param observable what was updated
     * @param oldValue   the old value
     * @param newValue   the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if (value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);

        //Fill
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0, width, height);


        //Border
        //gc.setStroke(Color.BLACK);
        //gc.strokeRect(0,0,width,height);

        gc.setStroke(Color.ANTIQUEWHITE);
        gc.strokeRoundRect(2, 2, width - 4, height - 4, 10, 10);
        //String imgPath = "C:/Comp1206/coursework/src/main/resources/images/femboy.jpg";
        //Image image = new Image(imgPath, width,height ,false,false);
        //gc.drawImage(image, x, y);

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(5);
        innerShadow.setColor(Color.GRAY);
        gc.applyEffect(innerShadow);
    }

    /**
     * Paint this canvas with the given colour
     *
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);

        //Colour fill

        gc.setFill(colour);
        gc.fillRoundRect(2, 2, width - 4, height - 4, 10, 10);

        //Border
        gc.setStroke(colour);
        gc.strokeRoundRect(2, 2, width - 4, height - 4, 10, 10);

        gc.setFill(darker((Color) colour,0.75));
        double innerSquareSize = Math.min(width, height) * 0.75;
        double innerSquareX = (width - innerSquareSize) / 2;
        double innerSquareY = (height - innerSquareSize) / 2;
        gc.fillRect(innerSquareX, innerSquareY, innerSquareSize, innerSquareSize);


        if (width == 50 && height == 50 && x == 1 && y == 1) {
            // Draw a circle indicator on the middle block
            gc.setFill(new Color(0, 0, 0, 0.5));
            double circleRadius = width / 4; // Adjust the size of the circle as needed
            gc.fillOval(width / 2 - circleRadius, height / 2 - circleRadius, 2 * circleRadius, 2 * circleRadius);
        }


    }



    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    /**
     * Animation for the fading out of the current block when a line is cleared
     * Uses a Fade transition to simulate the blinking and fading out of the block
     * Repaints the value to 0 (empty/transparent block) after animation had finished.
     */
    public void fadeOut() {
        FadeTransition flashTransition = new FadeTransition(Duration.millis(100), this);
        flashTransition.setFromValue(1);
        flashTransition.setToValue(0.5);
        flashTransition.setAutoReverse(true);
        flashTransition.setCycleCount(1);

        FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(1000), this);
        fadeOutTransition.setFromValue(1);
        fadeOutTransition.setToValue(0);

        flashTransition.setOnFinished(event -> fadeOutTransition.play());
        flashTransition.play();

        fadeOutTransition.setOnFinished(event -> {
            FadeTransition fadeInTransition = new FadeTransition(Duration.millis(1), this);
            fadeInTransition.setFromValue(0);
            fadeInTransition.setToValue(1);
            fadeInTransition.play();
        });


    }
    /**
     * Darkens the given color by adjusting its brightness.
     * @param color  the original color to be darkened
     * @param factor the factor by which to darken the color (0.0 to 1.0, where 0.0 is darkest)
     * @return the darkened color
     */
    private Color darker(Color color, double factor) {
        // Adjust brightness to make the color darker
        return color.deriveColor(0, 1, factor, 1);
    }
}
