package commands;

import collection.Organization;
import collection.Product;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import core.Creator;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.*;
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
        tag = interpreter.getTag();
        try {
            if (isInteractive) {
                if (!arg.matches("\\s*")) {
                    throw new IllegalArgumentException(getStringFromBundle("addIfMaxInteractiveError"));
                }
            } else {
                if (!arg.matches("\\s*\\{.*}\\s*")) {
                    throw new IllegalArgumentException(getStringFromBundle("addIfMaxNotInteractiveError"));
                } else {
                    Matcher m = Pattern.compile("\\{.*}").matcher(arg);
                    if (m.find()) {
                        product = new Gson().fromJson(m.group(), Product.class);
                    }
                }
            }
            product = Creator.createProduct(product, isInteractive);
            if (product == null) {
                content = getStringFromBundle("productCreationError");
                return false;
            }
        } catch (JsonSyntaxException | NumberFormatException e) {
            content = getStringFromBundle("jsonError")+e.getMessage();
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
                    return "0";
                } else {
                    return getStringFromBundle("addIfMaxSqlError");
                }
            } else {
                return getStringFromBundle("addIfMaxError");
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
                return getStringFromBundle("addIfMaxSqlError2");
            }
        }
    }

    @Override
    public String description() {
        return getStringFromBundle("addIfMaxDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("addIfMaxSyntax");
    }
}
