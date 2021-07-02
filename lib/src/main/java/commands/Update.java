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

public class Update extends Command {
    private Product product;
    private Long id;

    public Update(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        tag = interpreter.getTag();
        Product product = null;
        try {
            if (isInteractive) {
                if (!arg.matches("\\s*\\d+\\s*")) {
                    throw new IllegalArgumentException(getStringFromBundle("updateInteractiveError"));
                } else {
                    Matcher m = Pattern.compile("\\d+").matcher(arg);
                    if (m.find()) {
                        try {
                            id = Long.parseLong(m.group());
                            if (id <= 0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(getStringFromBundle("updateInteractiveError"));
                        }
                    }
                }
            } else {
                if (!arg.matches("\\s*\\d+\\s+\\{.*}\\s*")) {
                    throw new IllegalArgumentException(getStringFromBundle("updateNotInteractiveError"));
                } else {
                    Matcher m = Pattern.compile("\\{.*}").matcher(arg);
                    if (m.find()) {
                        product = new Gson().fromJson(m.group(), Product.class);
                    }
                    m = Pattern.compile("\\d+").matcher(arg);
                    if (m.find()) {
                        try {
                            id = Long.parseLong(m.group());
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(getStringFromBundle("updateNotInteractiveError"));
                        }
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
        Optional<Product> optional = collection.stream().filter(x -> x.getId().equals(id)).findAny();
        if (!optional.isPresent()) {
            return getStringFromBundle("updateError1") + id + getStringFromBundle("updateError2");
        } else {
            Product product = optional.get();
            if (user.getName().equals("admin") || product.getUser().getName().equals(this.product.getUser().getName())) {
                this.product.setId(id);
                this.product.setUser(product.getUser());
                if (dbUnit.updateProductInDB(this.product)) {
                    this.product.setCreationDate(product.getCreationDate());
                    if (!product.getManufacturer().equals(this.product.getManufacturer())) {
                        if (collection.stream().filter(x -> x.getManufacturer().equals(product.getManufacturer())).count() == 1) {
                            organizations.remove(product.getManufacturer());
                        }
                        Optional<Organization> optional2 = organizations.stream().filter(x -> x.equals(this.product.getManufacturer())).findAny();
                        if (optional2.isPresent()) {
                            this.product.setManufacturer(optional2.get());
                        } else {
                            this.product.getManufacturer().createId(organizations);
                            organizations.add(product.getManufacturer());
                        }
                    }
                    collection.remove(product);
                    collection.add(this.product);
                    return "0";
                    //return "Элемент c id " + id + " успешно обновлён!";
                } else {
                    return getStringFromBundle("updateError3") + id + getStringFromBundle("removeError2");
                }
            } else {
                return getStringFromBundle("removeError3") + id + getStringFromBundle("removeError4");
            }
        }
    }

    @Override
    public String description() {
        return getStringFromBundle("updateDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("updateSyntax");
    }
}
