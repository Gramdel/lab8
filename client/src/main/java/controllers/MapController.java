package controllers;

import collection.Product;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;

import static core.Main.*;
import static core.WindowManager.*;

public class MapController extends Controller {

    @FXML
    public ChoiceBox<String> languageChoiceBox;

    @FXML
    public Circle languageCircle;

    @FXML
    public ImageView flag;

    @FXML
    public Label userLabel;

    @FXML
    public ImageView signoutImage;

    @FXML
    public Button proceedButton;

    @FXML
    public Pane paneForDrawing;

    private static ObservableList<Product> products;

    @FXML
    void initialize() {
        ObservableList<String> languages = FXCollections.observableArrayList("en-CA", "ru-RU", "sl-SI", "sq-AL");
        languages.forEach(x -> {
            if (!languageChoiceBox.getItems().contains(x)) {
                languageChoiceBox.getItems().add(x);
            }
        });
        languageChoiceBox.getSelectionModel().select(getCurrentBundleName());
        languageChoiceBox.setOnAction(event -> {
            setCurrentBundle(languageChoiceBox.getValue());
            initialize();
        });

        Tooltip.install(flag, getTooltipWithDelay("Change language", 10));

        flag.setImage(new Image("/images/" + getCurrentBundleName() + ".png"));

        flag.setOnMouseEntered(event -> getScene().setCursor(Cursor.HAND));

        flag.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        flag.setOnMouseClicked(event -> {
            languageChoiceBox.show();
        });

        Tooltip.install(signoutImage, getTooltipWithDelay(getStringFromBundle("signOut"), 10));

        signoutImage.setOnMouseEntered(event -> {
            getScene().setCursor(Cursor.HAND);
            signoutImage.setImage(new Image("/images/sing-out-hover.png"));
        });

        signoutImage.setOnMouseExited(event -> {
            getScene().setCursor(Cursor.DEFAULT);
            signoutImage.setImage(new Image("/images/sing-out-black.png"));
        });

        signoutImage.setOnMouseClicked(event -> {
            try {
                changeScene("start.fxml", "PRODMAN: " + getStringFromBundle("startWindowTitle"));
                //synchronizer.cancel();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "ERROR", getStringFromBundle("changeSceneError"), e.getMessage());
            }
        });

        userLabel.setText(getUser().getName());

        proceedButton.setText(getStringFromBundle("back"));

        proceedButton.setOnMouseEntered(event -> {
            getScene().setCursor(Cursor.HAND);
            proceedButton.setStyle("-fx-background-color: #4E3BEC");
        });

        proceedButton.setOnMouseExited(event -> {
            getScene().setCursor(Cursor.DEFAULT);
            proceedButton.setStyle("-fx-background-color: #1600D9");
        });

        proceedButton.setOnAction(event -> {
            proceedButton.setStyle("-fx-background-color: #3629A3");
            try {
                changeScene("main.fxml", "PRODMAN: " + getStringFromBundle("mainWindowTitle"));
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", getStringFromBundle("changeSceneError"), e.getMessage());
            }
        });

        setChangeSizeListeners();
        paneForDrawing.getChildren().clear();

        for (Product product : products) {
            setupProduct(product);
        }
    }

    private void setChangeSizeListeners() {
        paneForDrawing.widthProperty().addListener((ChangeListener<? super Number>) (observable, oldValue, newValue) -> {
            paneForDrawing.getChildren().clear();
            for (Product product : products) {
                setupProduct(product);
            }
        });

        paneForDrawing.heightProperty().addListener((ChangeListener<? super Number>) (observalbe, oldValue, newValue) -> {
            paneForDrawing.getChildren().clear();
            for (Product product : products) {
                setupProduct(product);
            }
        });
    }

    private void setupProduct(Product product) {
        float radius;
        if (product.getPrice() > 0) {
            radius = product.getPrice() / 70f;
        } else radius = 1;
        Circle circle = new Circle(radius * (paneForDrawing.getHeight() + paneForDrawing.getWidth() * 0.8) / 100);
        setCoordinates(circle, product.getCoordinates().getX(), product.getCoordinates().getY());

        paneForDrawing.getChildren().add(circle);

        ParallelTransition pt = new ParallelTransition();

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setDuration(Duration.millis(1000));
        scaleTransition.setNode(circle);
        scaleTransition.setByY(1.1);
        scaleTransition.setByX(1.1);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);

        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(1000));

        fade.setFromValue(0.1);
        fade.setToValue(10);

        fade.setCycleCount(1);
        fade.setNode(circle);
        pt.getChildren().add(scaleTransition);
        pt.getChildren().add(fade);
        pt.play();
    }

    private void setCoordinates(Circle circle, Double x, Long y) {
        double newX = x * paneForDrawing.getWidth() / 250 + paneForDrawing.getWidth() / 2;
        double newY = -y * paneForDrawing.getHeight() / 50 + paneForDrawing.getWidth() / 2;
        circle.setLayoutX(newX);
        circle.setLayoutY(newY);
    }

    public static void setProducts(ObservableList<Product> products) {
        MapController.products = products;
    }
}
