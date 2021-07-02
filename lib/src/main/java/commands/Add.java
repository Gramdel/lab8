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

public class Add extends Command {
    private Product product;

    public Add(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        tag = interpreter.getTag();
        Product product = null;
        try {
            if (isInteractive) {
                if (!arg.matches("\\s*")) {
                    throw new IllegalArgumentException(getStringFromBundle("addInteractiveError"));
                }
            } else {
                if (!arg.matches("\\s*\\{.*}\\s*")) {
                    throw new IllegalArgumentException(getStringFromBundle("addNotInteractiveError"));
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
            return getStringFromBundle("addSqlError");
        }
    }

    @Override
    public String description() {
        return  getStringFromBundle("addDescription")+ syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("addSyntax");
    }
}