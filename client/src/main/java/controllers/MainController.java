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
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

import static core.WindowManager.showAlert;

public class MainController extends Controller {

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
    private ChoiceBox<?> commandChoiceBox;

    @FXML
    private Line underlineCommandButton;

    @FXML
    private Button commandButton;

    @FXML
    void initialize() {
        fillTable();

        commandChoiceBox.setItems();
    }

    private void fillTable() {
        ObservableList<Product> observableList = FXCollections.observableArrayList();

        String jsonString = Client.sendCommandAndReceiveResult(new Show(Main.getUser()));
        if (jsonString != null) {
            try {
                JSONArray jsonArray = (JSONArray) new JSONParser().parse("[" + jsonString + "]");
                jsonArray.forEach(element -> {
                    try {
                        JSONObject o = (JSONObject) element;
                        Product product = new Product(o.get("name").toString(), new Gson().fromJson(o.get("coordinates").toString(), Coordinates.class), ((Double) o.get("price")).floatValue(), o.get("partNumber").toString(), ((Double) o.get("manufactureCost")).floatValue(), UnitOfMeasure.fromString(o.get("unitOfMeasure").toString()), new Gson().fromJson(o.get("manufacturer").toString(), Organization.class));
                        product.setId((Long) o.get("id"));
                        product.setCreationDate(LocalDateTime.from(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").parse(o.get("creationDate").toString())).atZone(ZoneId.of("GMT+3")));
                        product.setUser(new User(o.get("owner").toString()));
                        System.out.println(product);
                        observableList.add(product);
                    } catch (JsonSyntaxException e) {
                        showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при загрузке элементов коллекции", e.getMessage());
                    }
                });
            } catch (ParseException e) {
                showAlert(Alert.AlertType.ERROR, "ERROR", "Ошибка при загрузке элементов коллекции", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "INFO", "Коллекция пуста!", "Не загружено ни одного элемента.");
        }

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

        tableOfProducts.setItems(observableList);
    }
}