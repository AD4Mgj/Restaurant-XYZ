package kul.example;

import javafx.application.Application;
import javafx.stage.Stage;
import kul.example.utils.SceneUtils;


public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Restaurant XYZ");

        primaryStage.setResizable(false);

        SceneUtils.setInitialScene(primaryStage, "/view/loginScreen.fxml");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}