package controllers;

import collection.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import commands.Show;
import core.Client;
import core.Main;
import core.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Stack;

import static core.Main.getInterpreter;
import static core.Main.getUser;
import static core.WindowManager.*;
import static java.lang.Thread.sleep;

public class MainController extends Controller {

    @FXML
    public Button proceedButton;

    @FXML
    public ImageView signoutImage;

    @FXML
    public Label goBackLabel;

    @FXML
    private TableView<Product> tableOfProducts;

    @FXML
    private TableColumn<Product, Long> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Double> xColumn;

    @FXML
    private TableColumn<Product, Long> yColumn;

    @FXML
    private TableColumn<Product, Float> priceColumn;

    @FXML
    private TableColumn<Product, String> partNumberColumn;

    @FXML
    private TableColumn<Product, Float> manufactureCostColumn;

    @FXML
    private TableColumn<Product, UnitOfMeasure> unitOfMeasureColumn;

    @FXML
    private TableColumn<Product, String> manufacturerNameColumn;

    @FXML
    private TableColumn<Product, Long> annualTurnoverColumn;

    @FXML
    private TableColumn<Product, Long> employeesCountColumn;

    @FXML
    private TableColumn<Product, OrganizationType> typeColumn;

    @FXML
    private TableColumn<Product, ZonedDateTime> creationDateColumn;

    @FXML
    private TableColumn<Product, String> ownerColumn;

    @FXML
    private Circle languageCircle;

    @FXML
    private ChoiceBox<String> commandChoiceBox;

    @FXML
    private Line underlineCommandButton;

    @FXML
    private Button commandButton;

    @FXML
    private Label commandMirrorLabel;

    @FXML
    private AnchorPane commandsAnchorPane;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Label chosenCommandLabel;

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField xField;

    @FXML
    private TextField yField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField partNumField;

    @FXML
    private TextField manCostField;

    @FXML
    private TextField uomField;

    @FXML
    private TextField manNameField;

    @FXML
    private TextField turnoverField;

    @FXML
    private TextField empCountField;

    @FXML
    private TextField typeField;

    @FXML
    private Label userLabel;

    private final HashMap<String, Integer> commandsAndModes = new HashMap<>();

    private Product chosenProduct;

    private final SynchronizerService synchronizer = new SynchronizerService();

    private AlertType alertType;

    @FXML
    void initialize() {
        userLabel.setText(getUser().getName());

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        partNumberColumn.setCellValueFactory(new PropertyValueFactory<>("partNumber"));
        manufactureCostColumn.setCellValueFactory(new PropertyValueFactory<>("manufactureCost"));
        unitOfMeasureColumn.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasure"));
        manufacturerNameColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturerName"));
        annualTurnoverColumn.setCellValueFactory(new PropertyValueFactory<>("annualTurnover"));
        employeesCountColumn.setCellValueFactory(new PropertyValueFactory<>("employeesCount"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedCreationDate"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));

        commandsAndModes.put("add", 2);
        commandsAndModes.put("add_if_max", 2);
        commandsAndModes.put("clear", 0);
        commandsAndModes.put("execute_script", 0);
        commandsAndModes.put("history", 0);
        commandsAndModes.put("help", 0);
        commandsAndModes.put("remove_by_id", 0);
        commandsAndModes.put("remove_any_by_uom", 0);
        commandsAndModes.put("remove_greater", 2);
        commandsAndModes.put("update", 1);

        ObservableList<String> commands = FXCollections.observableArrayList();
        commands.addAll(commandsAndModes.keySet());
        commandChoiceBox.setItems(commands);

        Tooltip.install(commandMirrorLabel, getTooltipWithDelay("Выбрать команду", 10));

        commandMirrorLabel.setOnMouseClicked(event -> commandChoiceBox.show());

        commandMirrorLabel.setOnMouseEntered(event -> getScene().setCursor(Cursor.HAND));

        commandMirrorLabel.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        Tooltip.install(commandChoiceBox, getTooltipWithDelay("Выбрать команду", 10));

        commandChoiceBox.setOnAction(event -> commandMirrorLabel.setText(commandChoiceBox.getValue()));

        commandChoiceBox.setOnMouseEntered(event -> getScene().setCursor(Cursor.HAND));

        commandChoiceBox.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        commandButton.setOnAction(event -> {
            if (!commandMirrorLabel.getText().isEmpty()) {
                underlineCommandButton.setStroke(Color.web("#1600d9"));
                prepareCommand();
                getScene().setCursor(Cursor.DEFAULT);
            }
        });

        commandButton.setOnMouseEntered(event -> {
            if (!commandMirrorLabel.getText().isEmpty()) {
                getScene().setCursor(Cursor.HAND);
            }
        });

        commandButton.setOnMouseExited(event -> {
            if (!commandMirrorLabel.getText().isEmpty()) {
                underlineCommandButton.setStroke(Color.web("white"));
                getScene().setCursor(Cursor.DEFAULT);
            }
        });

        tableOfProducts.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            MenuItem itemUpdate = new MenuItem("Обновить");
            itemUpdate.setOnAction(event -> {
                chosenProduct = row.getItem();
                commandChoiceBox.setValue("update");
                commandMirrorLabel.setText("update");
                prepareCommand();
                setFields();
            });
            MenuItem itemRemove = new MenuItem("Удалить");
            itemRemove.setOnAction(event -> {
                String result = getInterpreter().fromString("remove_by_id", row.getItem().getId().toString());
                if (result == null) {
                    showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при отправке/получении команды", Client.getContent());
                } else if (result.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при проверке аргументов команды", getInterpreter().getContent());
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "INFO", "Команда " + commandMirrorLabel.getText() + " вернула следующий результат:", result);
                }
            });
            ContextMenu menu = new ContextMenu(itemUpdate, itemRemove);
            row.setOnMouseClicked(event -> {
                if (menu.isShowing()) {
                    menu.hide();
                }
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    chosenProduct = row.getItem();
                    commandMirrorLabel.setText("update");
                    prepareCommand();
                    setFields();
                }
            });
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    Tooltip.install(row, getTooltipWithDelay("Нажмите дважды, чтобы обновить элемент", 700));
                    if (!menu.isShowing()) {
                        getScene().setCursor(Cursor.HAND);
                    }
                }
            });
            row.setOnMouseExited(event -> {
                if (!row.isEmpty()) {
                    row.setTooltip(null);
                    getScene().setCursor(Cursor.DEFAULT);
                }
            });

            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    if (menu.isShowing()) {
                        menu.hide();
                    }
                    menu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });

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
            getScene().setCursor(Cursor.DEFAULT);
        });

        Tooltip.install(signoutImage, getTooltipWithDelay("Выйти", 10));

        signoutImage.setOnMouseEntered(event -> {
            getScene().setCursor(Cursor.HAND);
            signoutImage.setImage(new Image("/images/sing-out-hover.png"));
        });

        signoutImage.setOnMouseExited(event -> {
            getScene().setCursor(Cursor.DEFAULT);
            signoutImage.setImage(new Image("/images/sing-out.png"));
        });

        signoutImage.setOnMouseClicked(event -> {
            try {
                changeScene("start.fxml", "PRODMAN: Авторизация");
                synchronizer.cancel();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка смены сцены", e.getMessage());
            }
        });

        idField.setOnKeyPressed(event -> idField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        nameField.setOnKeyPressed(event -> nameField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        xField.setOnKeyPressed(event -> xField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        yField.setOnKeyPressed(event -> yField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        priceField.setOnKeyPressed(event -> priceField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        partNumField.setOnKeyPressed(event -> partNumField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        manCostField.setOnKeyPressed(event -> manCostField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        uomField.setOnKeyPressed(event -> uomField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        manNameField.setOnKeyPressed(event -> manNameField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        turnoverField.setOnKeyPressed(event -> turnoverField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        empCountField.setOnKeyPressed(event -> empCountField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));
        typeField.setOnKeyPressed(event -> typeField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;"));

        Tooltip.install(goBackLabel, getTooltipWithDelay("Обратно", 10));

        goBackLabel.setOnMouseEntered(event -> {
            getScene().setCursor(Cursor.HAND);
            goBackLabel.setTextFill(Color.web("#4E3BEC"));
        });

        goBackLabel.setOnMouseExited(event -> {
            getScene().setCursor(Cursor.DEFAULT);
            goBackLabel.setTextFill(Color.web("white"));
        });

        goBackLabel.setOnMouseClicked(event -> {
            goBackLabel.setTextFill(Color.web("#3629A3"));
            getScene().setCursor(Cursor.DEFAULT);
            commandsAnchorPane.setVisible(false);
            resetFields();
            mainAnchorPane.setVisible(true);
        });

        synchronizer.setOnCancelled(event -> {
            new Alert(Alert.AlertType.ERROR).show();
        });
        synchronizer.setOnSucceeded(event -> new Alert(alertType).show());
        synchronizer.start();
    }

    private class SynchronizerService extends Service<Void> {
        private String title;
        private String header;
        private String content;

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
                    long count = 0;
                    while (true) {
                        System.out.println(count);
                        if (fillTable(count++) == count) {
                            break;
                        }
                        try {
                            sleep(150);
                        } catch (InterruptedException e) {
                            break;
                        }
                        if (count == 0) {
                            count++;
                        }
                    }
                    return null;
                }
            };
        }
    }

    private long fillTable(long count) {
        ObservableList<Product> observableList = FXCollections.observableArrayList();

        String jsonString = Client.sendCommandAndReceiveResult(new Show(Main.getUser()));
        if (jsonString != null) {
            if (!jsonString.equals("0")) {
                try {
                    JSONArray jsonArray = (JSONArray) new JSONParser().parse("[" + jsonString + "]");
                    jsonArray.forEach(element -> {
                        try {
                            JSONObject o = (JSONObject) element;
                            Product product = new Product(o.get("name").toString(), new Gson().fromJson(o.get("coordinates").toString(), Coordinates.class), ((Double) o.get("price")).floatValue(), o.get("partNumber").toString(), ((Double) o.get("manufactureCost")).floatValue(), UnitOfMeasure.fromString(o.get("unitOfMeasure").toString()), new Gson().fromJson(o.get("manufacturer").toString(), Organization.class));
                            product.setId((Long) o.get("id"));
                            product.setCreationDate(LocalDateTime.from(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").parse(o.get("creationDate").toString())).atZone(ZoneId.of("GMT+3")));
                            product.setUser(new User(o.get("owner").toString()));
                            observableList.add(product);
                        } catch (JsonSyntaxException e) {
                            //showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при загрузке элементов коллекции", e.getMessage());
                        }
                    });
                } catch (ParseException e) {
                    //showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при загрузке элементов коллекции", e.getMessage());
                }
            } else if (count == 0) {
                //showAlert(Alert.AlertType.INFORMATION, "INFO", "Коллекция пуста!", "Не загружено ни одного элемента.");
            }
        } else {
            alertType = Alert.AlertType.ERROR;
            //title = title;
            //header = header;
            //content = content;
            return count+1;
            //String content = Client.getContent();
            //synchronizer.stop(Alert.AlertType.ERROR, "ERROR", "Ошибка при загрузке элементов коллекции", content);
            //showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при загрузке элементов коллекции", Client.getContent());
        }

        tableOfProducts.getItems().clear();
        tableOfProducts.getItems().addAll(observableList);
        return 0;
    }

    private void prepareCommand() {
        resetFields();
        mainAnchorPane.setVisible(false);
        idField.setVisible(false);
        idField.setEditable(true);
        idField.setPromptText("id");
        chosenCommandLabel.setText(commandMirrorLabel.getText());
        switch (commandsAndModes.get(commandMirrorLabel.getText())) {
            case 0:
                if (commandMirrorLabel.getText().equals("execute_script")) {
                    idField.setPromptText("file");
                    Tooltip.install(idField, getTooltipWithDelay("file", 10));
                    idField.setVisible(true);
                } else if (commandMirrorLabel.getText().equals("remove_any_by_uom")) {
                    idField.setPromptText("UOM");
                    Tooltip.install(idField, getTooltipWithDelay("UOM", 10));
                    idField.setVisible(true);
                } else if (commandMirrorLabel.getText().equals("remove_by_id")) {
                    idField.setVisible(true);
                }
                nameField.setVisible(false);
                xField.setVisible(false);
                yField.setVisible(false);
                priceField.setVisible(false);
                partNumField.setVisible(false);
                manCostField.setVisible(false);
                uomField.setVisible(false);
                manNameField.setVisible(false);
                turnoverField.setVisible(false);
                empCountField.setVisible(false);
                typeField.setVisible(false);
                break;
            case 1:
                idField.setVisible(true);
            case 2:
                nameField.setVisible(true);
                xField.setVisible(true);
                yField.setVisible(true);
                priceField.setVisible(true);
                partNumField.setVisible(true);
                manCostField.setVisible(true);
                uomField.setVisible(true);
                manNameField.setVisible(true);
                turnoverField.setVisible(true);
                empCountField.setVisible(true);
                typeField.setVisible(true);
                break;
        }
        commandsAnchorPane.setVisible(true);
    }

    private void setFields() {
        idField.setText(chosenProduct.getId().toString());
        idField.setEditable(false);
        nameField.setText(chosenProduct.getName());
        xField.setText(chosenProduct.getX().toString());
        yField.setText(chosenProduct.getY().toString());
        priceField.setText(String.valueOf(chosenProduct.getPrice()));
        partNumField.setText(chosenProduct.getPartNumber());
        manCostField.setText(chosenProduct.getManufactureCost().toString());
        uomField.setText(chosenProduct.getUnitOfMeasure().name());
        manNameField.setText(chosenProduct.getManufacturerName());
        try {
            turnoverField.setText(chosenProduct.getAnnualTurnover().toString());
        } catch (NullPointerException e) {
            turnoverField.setText("");
        }
        try {
            empCountField.setText(chosenProduct.getEmployeesCount().toString());
        } catch (NullPointerException e) {
            empCountField.setText("");
        }
        try {
            typeField.setText(chosenProduct.getType().name());
        } catch (NullPointerException e) {
            typeField.setText("");
        }
    }

    private void proceed() {
        String result;
        switch (commandsAndModes.get(commandMirrorLabel.getText())) {
            case 0:
                result = getInterpreter().fromString(commandMirrorLabel.getText(), idField.getText());
                if (result == null) {
                    showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при отправке/получении команды", Client.getContent());
                } else if (result.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при проверке аргументов команды", getInterpreter().getContent());
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "INFO", "Команда " + commandMirrorLabel.getText() + " вернула следующий результат:", result);
                }
                commandsAnchorPane.setVisible(false);
                mainAnchorPane.setVisible(true);
                break;
            case 1:
            case 2:
                Stack<String> errors = new Stack<>();
                Long id = null;
                if (idField.isVisible()) {
                    try {
                        id = Long.parseLong(idField.getText());
                        if (id <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        idField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                        errors.push("Неправильный ввод id! Требуемый формат: целое положительное число.\n");
                    }
                }
                String name = nameField.getText();
                if (name == null || name.matches("\\s*")) {
                    nameField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод названия продукта! Оно не может быть пустой строкой.\n");
                }
                Double x = null;
                try {
                    x = Double.parseDouble(xField.getText());
                } catch (NumberFormatException e) {
                    xField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод координаты x! Требуемый формат: дробное число.\n");
                }
                Long y = null;
                try {
                    y = Long.parseLong(yField.getText());
                } catch (NumberFormatException e) {
                    yField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод координаты y! Требуемый формат: целое число.\n");
                }
                float price = -1;
                try {
                    price = Float.parseFloat(priceField.getText());
                    if (price <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    priceField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод цены продукта! Требуемый формат: положительное дробное число.\n");
                }
                String partNumber = partNumField.getText();
                if (partNumber == null || !partNumber.matches("#\\d{6}")) {
                    partNumField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод кода производителя! Требуемый формат: #xxxxxx, где x - цифры.\n");
                }
                float manufactureCost = -1;
                try {
                    manufactureCost = Float.parseFloat(manCostField.getText());
                    if (manufactureCost <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    manCostField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод цены производства продукта! Требуемый формат: положительное дробное число.\n");
                }
                UnitOfMeasure unitOfMeasure = null;
                try {
                    unitOfMeasure = UnitOfMeasure.fromString(uomField.getText());
                    if (unitOfMeasure == null) {
                        throw new IllegalArgumentException();
                    }
                } catch (IllegalArgumentException e) {
                    uomField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод единиц измерения! Возможные варианты ввода: " + UnitOfMeasure.valueList() + ".\n");
                }
                String manufacturerName = manNameField.getText();
                if (manufacturerName == null || manufacturerName.matches("\\s*")) {
                    manNameField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод названия компании-производителя! Оно не может быть пустой строкой.\n");
                }
                Long annualTurnover = null;
                try {
                    if (!turnoverField.getText().isEmpty()) {
                        annualTurnover = Long.parseLong(turnoverField.getText());
                        if (annualTurnover <= 0) {
                            throw new NumberFormatException();
                        }
                    }
                } catch (NumberFormatException e) {
                    turnoverField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод ежегодного оборота компании-производителя! Требуемый формат: пустая строка или целое положительное число.\n");
                }
                Long employeesCount = null;
                try {
                    if (!empCountField.getText().isEmpty()) {
                        employeesCount = Long.parseLong(empCountField.getText());
                        if (employeesCount <= 0) {
                            throw new NumberFormatException();
                        }
                    }
                } catch (NumberFormatException e) {
                    empCountField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод количества сотрудников компании-производителя! Требуемый формат: пустая строка или целое положительное число.\n");
                }
                OrganizationType type = null;
                try {
                    if (!typeField.getText().isEmpty()) {
                        type = OrganizationType.fromString(typeField.getText());
                    }
                } catch (IllegalArgumentException e) {
                    typeField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: #ff2626;");
                    errors.push("Неправильный ввод типа компании-производителя! Возможные варианты ввода: пустая строка, " + OrganizationType.valueList() + ".\n");
                }
                if (errors.isEmpty()) {
                    Organization manufacturer = new Organization(manufacturerName, annualTurnover, employeesCount, type);
                    Product product = new Product(name, new Coordinates(x, y), price, partNumber, manufactureCost, unitOfMeasure, manufacturer);
                    product.setUser(getUser());
                    product.setId(id);
                    result = getInterpreter().fromString(commandMirrorLabel.getText(), (id != null ? id + " " : "") + product.toStringNoDate());
                    if (result == null) {
                        showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при отправке/получении команды", Client.getContent());
                    } else if (result.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при проверке аргументов команды", getInterpreter().getContent());
                    } else if (result.equals("0")) {
                        showAlert(Alert.AlertType.INFORMATION, "SUCCESS", "Команда " + commandMirrorLabel.getText() + " выполнена успешно!", "");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при выполнении команды", result);
                    }
                    commandsAnchorPane.setVisible(false);
                    mainAnchorPane.setVisible(true);
                } else {
                    StringBuilder builder = new StringBuilder();
                    errors.forEach(builder::append);
                    showAlert(Alert.AlertType.ERROR, "ERROR", "Воникли ошибки при проверке аргументов команды", builder.toString());
                }
                break;
        }
    }

    private void resetFields() {
        idField.clear();
        nameField.clear();
        xField.clear();
        yField.clear();
        priceField.clear();
        partNumField.clear();
        manCostField.clear();
        uomField.clear();
        manNameField.clear();
        turnoverField.clear();
        empCountField.clear();
        typeField.clear();

        idField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        nameField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        xField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        yField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        priceField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        partNumField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        manCostField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        uomField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        manNameField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        turnoverField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        empCountField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");
        typeField.setStyle("-fx-background-color: transparent; -fx-background-image: url('/images/field-bg.png'); -fx-text-fill: white;");

        Tooltip.install(idField, getTooltipWithDelay("id", 10));
        Tooltip.install(nameField, getTooltipWithDelay("name", 10));
        Tooltip.install(xField, getTooltipWithDelay("x", 10));
        Tooltip.install(yField, getTooltipWithDelay("y", 10));
        Tooltip.install(priceField, getTooltipWithDelay("price", 10));
        Tooltip.install(partNumField, getTooltipWithDelay("partNum", 10));
        Tooltip.install(uomField, getTooltipWithDelay("UOM", 10));
        Tooltip.install(manCostField, getTooltipWithDelay("manCost", 10));
        Tooltip.install(manNameField, getTooltipWithDelay("manName", 10));
        Tooltip.install(turnoverField, getTooltipWithDelay("turnover", 10));
        Tooltip.install(empCountField, getTooltipWithDelay("empCount", 10));
        Tooltip.install(typeField, getTooltipWithDelay("type", 10));
    }
}