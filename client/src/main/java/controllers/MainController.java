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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static core.Main.getInterpreter;
import static core.Main.getUser;
import static core.WindowManager.getScene;
import static core.WindowManager.showAlert;
import static java.lang.Thread.sleep;

public class MainController extends Controller {

    @FXML
    public Button proceedButton;

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
    private Label chosenCommand;

    @FXML
    private AnchorPane withArgsAnchorPane;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Label noArgsLabel;

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

        SynchronizerService synchronizer = new SynchronizerService();
        synchronizer.start();

        commandsAndModes.put("add", 1);
        commandsAndModes.put("add_if_max", 1);
        commandsAndModes.put("clear", 0);
        commandsAndModes.put("execute_script", 0);
        commandsAndModes.put("history", 0);
        commandsAndModes.put("help", 0);
        commandsAndModes.put("remove_by_id", 0);
        commandsAndModes.put("remove_any_by_uom", 0);
        commandsAndModes.put("remove_greater", 1);
        commandsAndModes.put("update", 1);

        ObservableList<String> commands = FXCollections.observableArrayList();
        commands.addAll(commandsAndModes.keySet());
        commandChoiceBox.setItems(commands);

        chosenCommand.setOnMouseClicked(event -> commandChoiceBox.show());

        chosenCommand.setOnMouseEntered(event -> getScene().setCursor(Cursor.HAND));

        chosenCommand.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        commandChoiceBox.setOnAction(event -> chosenCommand.setText(commandChoiceBox.getValue()));

        commandChoiceBox.setOnMouseEntered(event -> getScene().setCursor(Cursor.HAND));

        commandChoiceBox.setOnMouseExited(event -> getScene().setCursor(Cursor.DEFAULT));

        commandButton.setOnAction(event -> {
            if (!chosenCommand.getText().isEmpty()) {
                underlineCommandButton.setStroke(Color.web("#1600d9"));
                prepareCommand();
                getScene().setCursor(Cursor.DEFAULT);
            }
        });

        commandButton.setOnMouseEntered(event -> {
            if (!chosenCommand.getText().isEmpty()) {
                getScene().setCursor(Cursor.HAND);
            }
        });

        commandButton.setOnMouseExited(event -> {
            if (!chosenCommand.getText().isEmpty()) {
                underlineCommandButton.setStroke(Color.web("white"));
                getScene().setCursor(Cursor.DEFAULT);
            }
        });

        tableOfProducts.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            MenuItem itemUpdate = new MenuItem("Обновить");
            itemUpdate.setOnAction(event -> {
                chosenProduct = row.getItem();
                chosenCommand.setText("update");
                prepareCommand();
                setFields();
            });
            MenuItem itemRemove = new MenuItem("Удалить");
            itemRemove.setOnAction(event -> {
                getInterpreter().fromString("remove_by_id", row.getItem().getId().toString());
            });
            ContextMenu menu = new ContextMenu(itemUpdate, itemRemove);
            row.setOnMouseClicked(event -> {
                if (menu.isShowing()) {
                    menu.hide();
                }
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    chosenProduct = row.getItem();
                    chosenCommand.setText("update");
                    prepareCommand();
                    setFields();
                }
            });
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty() && !menu.isShowing()) {
                    getScene().setCursor(Cursor.HAND);
                }
            });
            row.setOnMouseExited(event -> {
                if (!row.isEmpty()) {
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
    }

    private void fillTable(int count) {
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
                            showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при загрузке элементов коллекции", e.getMessage());
                        }
                    });
                } catch (ParseException e) {
                    showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при загрузке элементов коллекции", e.getMessage());
                }
            } else if (count == 0) {
                showAlert(Alert.AlertType.INFORMATION, "INFO", "Коллекция пуста!", "Не загружено ни одного элемента.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при загрузке элементов коллекции", Client.getContent());
        }

        tableOfProducts.getItems().clear();
        tableOfProducts.getItems().addAll(observableList);
    }

    private class SynchronizerService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
                    int count = 0;
                    while (true) {
                        fillTable(count++);
                        try {
                            sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (count == 0) {
                            count++;
                        }
                    }
                }
            };
        }
    }

    private void prepareCommand() {
        mainAnchorPane.setVisible(false);
        noArgsLabel.setText(chosenCommand.getText());
        switch (commandsAndModes.get(chosenCommand.getText())) {
            case 0:
                idField.setVisible(false);
                idField.setPromptText("id");
                if (chosenCommand.getText().equals("execute_script")) {
                    idField.setPromptText("file");
                    idField.setVisible(true);
                } else if (chosenCommand.getText().equals("remove_any_by_uom")) {
                    idField.setPromptText("UOM");
                    idField.setVisible(true);
                } else if (chosenCommand.getText().equals("remove_by_id")) {
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
        withArgsAnchorPane.setVisible(true);
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

    }
}