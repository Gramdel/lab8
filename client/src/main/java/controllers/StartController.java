package controllers;

import core.Client;
import core.InterpreterForClient;
import core.Main;
import core.User;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.IOException;

import static core.WindowManager.*;

public class StartController extends Controller {

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
        authorisationButton.setOnAction(event -> {
            if (mode == 1) {
                mode = 0;
                authorisationButton.setTextFill(Color.web("#ffffff"));
                registrationButton.setTextFill(Color.web("#838aa2"));
                underlineLogin.setVisible(true);
                underlineRegister.setVisible(false);
                proceedButton.setText("ВХОД");
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
                proceedButton.setText("РЕГИСТРАЦИЯ");
                errorLabel.setText("");
                getScene().setCursor(Cursor.DEFAULT);
                loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
                passwordField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
            }
        });

        Tooltip.install(loginField, getTooltipWithDelay("Логин", 300));

        loginField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            } else {
                loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
            }
        });

        Tooltip.install(passwordField, getTooltipWithDelay("Пароль", 300));

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
    }

    void proceed() {
        getScene().setCursor(Cursor.WAIT);
        String name = loginField.getText().trim();
        String password = passwordField.getText().trim();
        boolean noErrors = true;
        if (name.isEmpty()) {
            errorLabel.setText("Имя пользователя не может\nбыть пустым!");
            loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
            noErrors = false;
        } else if (name.length() > 20) {
            errorLabel.setText("Имя пользователя не может\nбыть длиннее 20 символов!");
            loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
            noErrors = false;
        }

        if (password.isEmpty()) {
            errorLabel.setText("Пароль не может быть пустым!");
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
                            changeScene("main.fxml", "PRODMAN: Управление коллекцией продуктов");
                        } catch (IOException e) {
                            showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка смены сцены", e.getMessage());
                        }
                        break;
                    case 1:
                        errorLabel.setText("Неправильный пароль!");
                        passwordField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                        break;
                    case 2:
                        errorLabel.setText("Такого пользователя не существует!");
                        loginField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                        break;
                    case 3:
                        errorLabel.setText("Сервер не смог подключиться к базе данных!\nПопробуйте ещё раз.");
                        break;
                    case 4:
                        errorLabel.setText("Такой пользователь\nуже зарегистрирован!");
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
