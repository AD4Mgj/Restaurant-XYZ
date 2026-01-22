package kul.example.utils;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

import javafx.fxml.FXMLLoader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SceneUtils {
    private static final Logger LOGGER = Logger.getLogger(SceneUtils.class.getName());
    private static final double WIDTH = 1100;
    private static final double HEIGHT = 700;

    /**
     * Zmienia scenę na podaną i ustawia rozmiar na 1100x700.
     *
     * @param event    Zdarzenie (np. kliknięcie przycisku).
     * @param fxmlFile Ścieżka do pliku FXML.
     */
    public static void changeScene(MouseEvent event, String fxmlFile) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneUtils.class.getResource(fxmlFile)));

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            stage.setScene(scene);

            Platform.runLater(stage::centerOnScreen);

            stage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error changing scene", e);
        }
    }

    /**
     * Zmienia scenę na podaną na podstawie aktualnie otwartego okna (bez MouseEvent).
     *
     * @param fxmlFile Ścieżka do pliku FXML.
     */
    public static void changeScene(String fxmlFile) {
        try {
            Stage stage = (Stage) Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No active stage found"));

            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneUtils.class.getResource(fxmlFile)));

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            stage.setScene(scene);

            Platform.runLater(stage::centerOnScreen);

            stage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error changing scene", e);
        }
    }


    /**
     * Ustawia scenę na podanym Stage z domyślnym rozmiarem.
     *
     * @param stage    Główne okno aplikacji.
     * @param fxmlFile Ścieżka do pliku FXML.
     */
    public static void setInitialScene(Stage stage, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneUtils.class.getResource(fxmlFile)));

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            stage.setScene(scene);

            Platform.runLater(stage::centerOnScreen);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting initial scene", e);
        }
    }
}
