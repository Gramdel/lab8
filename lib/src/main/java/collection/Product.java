package collection;

import core.User;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashSet;

public class Product implements Comparable<Product>, Serializable {
    public static Comparator<Product> byIdComparator = (p1, p2) -> {
        if (p1.id.equals(p2.id)) {
            System.out.println("Что-то пошло не так: у двух продуктов один id!");
            return 0;
        }
        return (p1.id < p2.id) ? -1 : 1;
    };
    public static Comparator<Product> byPriceComparator = (p1, p2) -> Float.compare(p1.price, p2.price);
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Float price; //Значение поля должно быть больше 0
    private String partNumber; //Значение этого поля должно быть уникальным, Поле не может быть null
    private Float manufactureCost; //Поле не может быть null
    private UnitOfMeasure unitOfMeasure; //Поле не может быть null
    private User user;
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Organization manufacturer; //Поле может быть null
    private Double x;
    private Long y;
    private String manufacturerName; //Поле не может быть null, Строка не может быть пустой
    private Long annualTurnover; //Поле может быть null, Значение поля должно быть больше 0
    private Long employeesCount; //Поле может быть null, Значение поля должно быть больше 0
    private OrganizationType type; //Поле может быть null
    private String owner;

    public Product() {
        this.creationDate = ZonedDateTime.now();
    }

    public Product(String name, Coordinates coordinates, Float price, String partNumber, Float manufactureCost, UnitOfMeasure unitOfMeasure, Organization manufacturer) {
        this.name = name;
        this.coordinates = coordinates;
        x = coordinates.getX();
        y = coordinates.getY();
        this.creationDate = ZonedDateTime.now();
        this.price = price;
        this.partNumber = partNumber;
        this.manufactureCost = manufactureCost;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturer = manufacturer;
        manufacturerName = manufacturer.getName();
        annualTurnover = manufacturer.getAnnualTurnover();
        employeesCount = manufacturer.getEmployeesCount();
        type = manufacturer.getType();
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\" : " + id + "," +
                "\"name\" : \"" + name + "\"," +
                "\"coordinates\" : " + coordinates + "," +
                "\"creationDate\" : \"" + (DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(creationDate)) + "\"," +
                "\"price\" : " + price + "," +
                "\"partNumber\" : \"" + partNumber + "\"," +
                "\"manufactureCost\" : " + manufactureCost + "," +
                "\"unitOfMeasure\" : \"" + unitOfMeasure + "\"," +
                "\"manufacturer\" : " + manufacturer + "," +
                "\"owner\" : \"" + user.getName() + "\"" +
                "}";
    }

    public String toStringNoDate() {
        return "{" +
                "\"id\" : " + id + ", " +
                "\"name\" : \"" + name + "\", " +
                "\"coordinates\" : " + coordinates + ", " +
                "\"price\" : " + price + ", " +
                "\"partNumber\" : \"" + partNumber + "\", " +
                "\"manufactureCost\" : " + manufactureCost + ", " +
                "\"unitOfMeasure\" : \"" + unitOfMeasure + "\", " +
                "\"manufacturer\" : " + manufacturer + "," +
                "\"owner\" : \"" + user.getName() + "\"" +
                "}";
    }

    public String getPartNumber() {
        return partNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Organization getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Organization manufacturer) {
        this.manufacturer = manufacturer;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public String getFormattedCreationDate() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(creationDate);
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public int compareTo(Product product) {
        return this.creationDate.compareTo(product.creationDate);
    }

    public void createId(LinkedHashSet<Product> collection) {
        if (collection.isEmpty()) {
            id = 1L;
        } else {
            collection.stream().max(byIdComparator).ifPresent(x -> id = x.getId() + 1);
        }
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Float getManufactureCost() {
        return manufactureCost;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        owner = user.getName();
    }

    public Double getX() {
        return coordinates.getX();
    }

    public Long getY() {
        return coordinates.getY();
    }

    public String getOwner() {
        return user.getName();
    }

    public Long getAnnualTurnover() {
        return manufacturer.getAnnualTurnover();
    }

    public Long getEmployeesCount() {
        return manufacturer.getEmployeesCount();
    }

    public OrganizationType getType() {
        return manufacturer.getType();
    }

    public String getManufacturerName() {
        return manufacturer.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setManufactureCost(Float manufactureCost) {
        this.manufactureCost = manufactureCost;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}