package controllers;

import core.Client;
import core.InterpreterForClient;
import core.Main;
import core.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.IOException;

import static core.Main.*;
import static core.WindowManager.*;

public class StartController extends Controller {

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public ChoiceBox<String> languageChoiceBox;

    @FXML
    public ImageView flag;

    private int mode;

    @FXML
    private Button authorisationButton;

    @FXML
    private Button registrationButton;

    @FXML
    private Line underlineLogin;

    @FXML
    private Line underlineRegister;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button proceedButton;

    @FXML
    void initialize() {
        authorisationButton.setText(getStringFromBundle("authorisation"));
        registrationButton.setText(getStringFromBundle("registration"));
        proceedButton.setText(getStringFromBundle("proceedSignIn"));
        loginField.setPromptText(getStringFromBundle("login"));
        passwordField.setPromptText(getStringFromBundle("password"));

        authorisationButton.setOnAction(event -> {
            if (mode == 1) {
                mode = 0;
                authorisationButton.setTextFill(Color.web("#ffffff"));
                registrationButton.setTextFill(Color.web("#838aa2"));
                underlineLogin.setVisible(true);
                underlineRegister.setVisible(false);
                proceedButton.setText(getStringFromBundle("proceedSignIn"));
                errorLabel.setText("");
                getScene().setCursor(Cursor.DEFAULT);
                loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
                passwordField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
            }
        });

        authorisationButton.setOnMouseEntered(event -> {
            if (mode == 1) {
                getScene().setCursor(Cursor.HAND);
                authorisationButton.setTextFill(Color.web("#BEB7BF"));
            }
        });

        authorisationButton.setOnMouseExited(event -> {
            if (mode == 1) {
                getScene().setCursor(Cursor.DEFAULT);
                authorisationButton.setTextFill(Color.web("#838aa2"));
            }
        });

        registrationButton.setOnMouseEntered(event -> {
            if (mode == 0) {
                getScene().setCursor(Cursor.HAND);
                registrationButton.setTextFill(Color.web("#BEB7BF"));
            }
        });

        registrationButton.setOnMouseExited(event -> {
            if (mode == 0) {
                getScene().setCursor(Cursor.DEFAULT);
                registrationButton.setTextFill(Color.web("#838aa2"));
            }
        });

        registrationButton.setOnAction(event -> {
            if (mode == 0) {
                mode = 1;
                authorisationButton.setTextFill(Color.web("#838aa2"));
                registrationButton.setTextFill(Color.web("#ffffff"));
                underlineLogin.setVisible(false);
                underlineRegister.setVisible(true);
                proceedButton.setText(getStringFromBundle("proceedSignUp"));
                errorLabel.setText("");
                getScene().setCursor(Cursor.DEFAULT);
                loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
                passwordField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
            }
        });

        Tooltip.install(loginField, getTooltipWithDelay(getStringFromBundle("login"), 300));

        loginField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            } else {
                loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
            }
        });

        Tooltip.install(passwordField, getTooltipWithDelay(getStringFromBundle("password"), 300));

        passwordField.setOnKeyPressed(event -> {
            if (!proceedButton.isArmed()) {
                proceedButton.setStyle("-fx-background-color: #1600D9");
            }
            if (event.getCode() == KeyCode.ENTER) {
                proceedButton.fire();
            } else {
                passwordField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
            }
        });

        passwordField.setOnMouseMoved(event -> {
            if (!proceedButton.isArmed()) {
                proceedButton.setStyle("-fx-background-color: #1600D9");
            }
        });

        proceedButton.arm();
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
            proceed();
            proceedButton.disarm();
        });

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
    }

    void proceed() {
        getScene().setCursor(Cursor.WAIT);
        String name = loginField.getText().trim();
        String password = passwordField.getText().trim();
        boolean noErrors = true;
        if (name.isEmpty()) {
            errorLabel.setText(getStringFromBundle("emptyLoginError"));
            loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
            noErrors = false;
        } else if (name.length() > 20) {
            errorLabel.setText(getStringFromBundle("longLoginError"));
            loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
            noErrors = false;
        }

        if (password.isEmpty()) {
            errorLabel.setText(getStringFromBundle("emptyPasswordError"));
            passwordField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
            noErrors = false;
        }

        if (noErrors) {
            User user = new User(name, password);
            user.setCheckOrAdd(mode == 0);
            user = Client.sendAndReceiveUser(user);
            if (user != null) {
                switch (user.getErrorId()) {
                    case 0:
                        Main.setUser(user);
                        Main.setInterpreter(new InterpreterForClient(user));
                        try {
                            changeScene("main.fxml", "PRODMAN: " + getStringFromBundle("mainWindowTitle"));
                        } catch (IOException e) {
                            showAlert(Alert.AlertType.ERROR, "ERROR", getStringFromBundle("changeSceneError"), e.getMessage());
                        }
                        break;
                    case 1:
                        errorLabel.setText(getStringFromBundle("wrongPassword"));
                        passwordField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                        break;
                    case 2:
                        errorLabel.setText(getStringFromBundle("userNotExists"));
                        loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                        break;
                    case 3:
                        errorLabel.setText(getStringFromBundle("serverCantConnectToDB"));
                        break;
                    case 4:
                        errorLabel.setText(getStringFromBundle("userAlreadyExists"));
                        loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                        break;
                }
            } else {
                String header = mode == 0 ? "Login error" : "Registration error";
                showAlert(Alert.AlertType.ERROR, "ERROR", header, Client.getContent());
                proceedButton.setStyle("-fx-background-color: #1600D9");
            }
        }
        getScene().setCursor(Cursor.DEFAULT);
    }
}
