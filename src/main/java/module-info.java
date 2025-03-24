/**
 * Defines the module 'uk.ac.soton.comp1206', which encompasses the components of a specific application.
 * This module requires various JavaFX modules for user interface and media handling, as well as external libraries
 * for logging and WebSocket client functionality. It exports specific packages for external use and opens
 * the 'uk.ac.soton.comp1206.ui' package for use by JavaFX's FXML loader.
 */
module uk.ac.soton.comp1206 {
    requires java.scripting;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.apache.logging.log4j;
    requires nv.websocket.client;
    opens uk.ac.soton.comp1206.ui to javafx.fxml;
    exports uk.ac.soton.comp1206;
    exports uk.ac.soton.comp1206.ui;
    exports uk.ac.soton.comp1206.network;
    exports uk.ac.soton.comp1206.scene;
    exports uk.ac.soton.comp1206.event;
    exports uk.ac.soton.comp1206.component;
    exports uk.ac.soton.comp1206.game;
}