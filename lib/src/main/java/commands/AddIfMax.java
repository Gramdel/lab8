package commands;

import collection.Organization;
import collection.Product;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import core.Creator;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddIfMax extends Command {
    private Product product;

    public AddIfMax(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        Product product = null;
        try {
            if (isInteractive) {
                if (!arg.matches("\\s*")) {
                    throw new IllegalArgumentException("У команды add_if_max не может быть аргументов!");
                }
            } else {
                if (!arg.matches("\\s*\\{.*}\\s*")) {
                    throw new IllegalArgumentException("У команды add_if_max должен быть 1 аргумент: JSON-строка!");
                } else {
                    Matcher m = Pattern.compile("\\{.*}").matcher(arg);
                    if (m.find()) {
                        product = new Gson().fromJson(m.group(), Product.class);
                    }
                }
            }
            product = Creator.createProduct(product, isInteractive);
            if (product == null) {
                content = "Команда add_if_max не выполнена, т.к. не получилось создать продукт!";
                return false;
            }
        } catch (JsonSyntaxException | NumberFormatException e) {
            content = "Ошибка в синтаксисе JSON-строки! "+e.getMessage();
            return false;
        } catch (IllegalArgumentException e) {
            content = e.getMessage();
            return false;
        }
        product.setUser(user);
        this.product = product;
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        if (!collection.isEmpty()) {
            if (product.getPrice() >= collection.stream().max(Product.byPriceComparator).get().getPrice()) {
                product.createId(collection);
                if (dbUnit.addProductToDB(product)) {
                    Optional<Organization> optional = organizations.stream().filter(x -> x.equals(product.getManufacturer())).findAny();
                    if (optional.isPresent()) {
                        product.setManufacturer(optional.get());
                    } else {
                        product.getManufacturer().createId(organizations);
                        organizations.add(product.getManufacturer());
                    }
                    collection.add(product);
                    //return "Элемент успешно добавлен, т.к. его цена - наибольшая в коллекции!";
                    return "0";
                } else {
                    return "Несмотря на то, что цена элемента - наибольшая, он не был добавлен из-за ошибки SQL!";
                }
            } else {
                return "Элемент не добавлен, т.к. его цена - НЕ наибольшая в коллекции.";
            }
        } else {
            product.createId(collection);
            if (dbUnit.addProductToDB(product)) {
                Optional<Organization> optional = organizations.stream().filter(x -> x.equals(product.getManufacturer())).findAny();
                if (optional.isPresent()) {
                    product.setManufacturer(optional.get());
                } else {
                    product.getManufacturer().createId(organizations);
                    organizations.add(product.getManufacturer());
                }
                collection.add(product);
                return "0";
            } else {
                return "Несмотря на то, что цена элемента - наибольшая (коллекция пуста), он не был добавлен из-за ошибка SQL!";
            }
        }
    }

    @Override
    public String description() {
        return "Добавляет новый элемент в коллекцию, если его цена - наибольшая в коллекции." + syntax();
    }

    @Override
    public String syntax() {
        return " Синтаксис: add_if_max \n\t\t(В скриптах - add_if_max {element}, где {element} - JSON-строка)";
    }
}
