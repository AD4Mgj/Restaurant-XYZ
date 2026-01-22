package kul.example.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SceneUtils {

    private static final Logger LOGGER = Logger.getLogger(SceneUtils.class.getName());
    private static final double WIDTH = 1100;
    private static final double HEIGHT = 700;

    private static String normalizePath(String fxmlFile) {
        return fxmlFile.startsWith("/") ? fxmlFile : "/" + fxmlFile;
    }

    private static Parent loadFXML(String fxmlFile) throws Exception {
        String path = normalizePath(fxmlFile);
        return FXMLLoader.load(Objects.requireNonNull(
                SceneUtils.class.getResource(path),
                "Nie znaleziono pliku FXML: " + path
        ));
    }

    public static void changeScene(MouseEvent event, String fxmlFile) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = loadFXML(fxmlFile);

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            stage.setScene(scene);

            Platform.runLater(stage::centerOnScreen);
            stage.show();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error changing scene", e);
        }
    }

    public static void changeScene(String fxmlFile) {
        try {
            Stage stage = (Stage) Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No active stage found"));

            Parent root = loadFXML(fxmlFile);

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            stage.setScene(scene);

            Platform.runLater(stage::centerOnScreen);
            stage.show();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error changing scene", e);
        }
    }

    public static void setInitialScene(Stage stage, String fxmlFile) {
        try {
            Parent root = loadFXML(fxmlFile);

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            stage.setScene(scene);

            Platform.runLater(stage::centerOnScreen);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting initial scene", e);
        }
    }
}