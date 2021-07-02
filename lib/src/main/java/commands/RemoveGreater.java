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

public class RemoveGreater extends Command {
    private Product product;

    public RemoveGreater(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        tag = interpreter.getTag();
        Product product = null;
        try {
            if (isInteractive) {
                if (!arg.matches("\\s*")) {
                    throw new IllegalArgumentException(getStringFromBundle("rgInteractiveError"));
                }
            } else {
                if (!arg.matches("\\s*\\{.*}\\s*")) {
                    throw new IllegalArgumentException(getStringFromBundle("rgNotInteractiveError"));
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
        this.product = product;
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        int prevSize = collection.size();
        if (prevSize == 0) {
            return getStringFromBundle("rgError");
        } else {
            StringBuilder s = new StringBuilder();
            for (Iterator<Product> iter = collection.iterator(); iter.hasNext(); ) {
                Product product = iter.next();
                if (this.product.getPrice() < product.getPrice()) {
                    if (user.getName().equals("admin") || product.getUser().getName().equals(user.getName())) {
                        if (dbUnit.removeProductFromDB(product)) {
                            if (collection.stream().filter(x -> x.getManufacturer().equals(product.getManufacturer())).count() == 1) {
                                organizations.remove(product.getManufacturer());
                            }
                            iter.remove();
                        } else {
                            s.append(getStringFromBundle("removeError1")).append(product.getId()).append(getStringFromBundle("removeError2")).append("\n");
                        }
                    } else {
                        s.append(getStringFromBundle("removeError3")).append(product.getId()).append(getStringFromBundle("removeError4")).append("\n");
                    }
                }
            }
            if (prevSize > collection.size()) {
                if (s.toString().isEmpty()) {
                    return "0";
                } else {
                    return s + getStringFromBundle("rgError1");
                }
            } else {
                return s + getStringFromBundle("rgError2");
            }
        }
    }

    @Override
    public String description() {
        return getStringFromBundle("rgDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("rgSyntax");
    }
}
