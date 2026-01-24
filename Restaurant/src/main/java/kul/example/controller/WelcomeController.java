package kul.example.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import kul.example.utils.SceneUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WelcomeController {

    @FXML private ImageView sliderImageView;
    @FXML private HBox dotsBox;

    private final List<String> imagePaths = List.of(
            "/images/slider/pasta.png",
            "/images/slider/burger.png",
            "/images/slider/pizza.png",
            "/images/slider/cake.png"
    );

    private final List<Image> images = new ArrayList<>();
    private int currentIndex = 0;
    private Timeline timeline;

    @FXML
    public void initialize() {
        loadImages();
        setupDots();
        showImage(0);
        startAutoSlide();
    }

    private void loadImages() {
        images.clear();

        for (String path : imagePaths) {
            URL url = getClass().getResource(path);
            if (url != null) {
                images.add(new Image(url.toExternalForm()));
            }
        }

        // Fallback: jak ktoś nie wrzucił jeszcze obrazków z figmy
        if (images.isEmpty()) {
            URL fallback = getClass().getResource("/images/test.png");
            if (fallback != null) {
                images.add(new Image(fallback.toExternalForm()));
            }
        }
    }

    private void setupDots() {
        dotsBox.getChildren().clear();

        for (int i = 0; i < images.size(); i++) {
            Circle dot = new Circle(5);
            dot.getStyleClass().add("dot");

            final int idx = i;
            dot.setOnMouseClicked(e -> {
                showImage(idx);
                restartAutoSlide();
            });

            dotsBox.getChildren().add(dot);
        }

        updateDots();
    }

    private void showImage(int index) {
        if (images.isEmpty()) return;

        currentIndex = (index % images.size() + images.size()) % images.size();
        sliderImageView.setImage(images.get(currentIndex));
        updateDots();
    }

    private void nextImage() {
        showImage(currentIndex + 1);
    }

    private void updateDots() {
        for (int i = 0; i < dotsBox.getChildren().size(); i++) {
            Node n = dotsBox.getChildren().get(i);

            n.getStyleClass().remove("dot-active");
            if (i == currentIndex) {
                n.getStyleClass().add("dot-active");
            }
        }
    }

    private void startAutoSlide() {
        stopAutoSlide();

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> nextImage())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void restartAutoSlide() {
        if (timeline == null) return;
        timeline.stop();
        timeline.playFromStart();
    }

    private void stopAutoSlide() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    @FXML
    public void onLoginClick(ActionEvent event) {
        stopAutoSlide();
        SceneUtils.changeScene("/view/login.fxml");
    }

    @FXML
    public void onRegisterClick(ActionEvent event) {
        stopAutoSlide();
        SceneUtils.changeScene("/view/registerEmail.fxml");
    }
}