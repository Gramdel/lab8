package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.*;

public class Clear extends Command {
    public Clear(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        tag = interpreter.getTag();
        if (!arg.matches("\\s*")) {
            content = getStringFromBundle("clearError");
            return false;
        }
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        if (collection.size() > 0) {
            if (!user.getName().equals("admin")) {
                if (collection.stream().anyMatch(x -> x.getUser().getName().equals(user.getName()))) {
                    boolean noPermissionErrors = true;
                    boolean noSqlErrors = true;
                    for (Iterator<Product> iter = collection.iterator(); iter.hasNext(); ) {
                        Product product = iter.next();
                        if (product.getUser().getName().equals(user.getName())) {
                            if (dbUnit.removeProductFromDB(product)) {
                                if (collection.stream().filter(y -> y.getManufacturer().equals(product.getManufacturer())).count() == 1) {
                                    organizations.remove(product.getManufacturer());
                                }
                                iter.remove();
                            } else {
                                noSqlErrors = false;
                            }
                        } else {
                            noPermissionErrors = false;
                        }
                    }
                    if (!noPermissionErrors && !noSqlErrors) {
                        return getStringFromBundle("clearError1");
                    } else if (noPermissionErrors && !noSqlErrors) {
                        return getStringFromBundle("clearError2");
                    } else if (!noPermissionErrors) {
                        return getStringFromBundle("clearError3");
                    } else {
                        return getStringFromBundle("clearSuccess");
                    }
                } else {
                    return getStringFromBundle("clearError4");
                }
            } else {
                boolean noErrors = true;
                for (Iterator<Product> iter = collection.iterator(); iter.hasNext(); ) {
                    Product product = iter.next();
                    if (dbUnit.removeProductFromDB(product)) {
                        if (collection.stream().filter(y -> y.getManufacturer().equals(product.getManufacturer())).count() == 1) {
                            organizations.remove(product.getManufacturer());
                        }
                        iter.remove();
                    } else {
                        noErrors = false;
                    }
                }
                if (noErrors) {
                    return getStringFromBundle("clearSuccess");
                } else {
                    return getStringFromBundle("clearError2");
                }
            }
        }
        return getStringFromBundle("clearError5");
    }

    @Override
    public String description() {
        return getStringFromBundle("clearDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("clearSyntax");
    }
}
