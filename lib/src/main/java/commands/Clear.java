package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Clear extends Command {
    public Clear(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        if (!arg.matches("\\s*")) {
            content = "У команды clear не может быть аргументов!";
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
                        return "Не все элементы были удалены, т.к. возникли ошибки SQL и вы не являетесь владельцем некоторых элементов!";
                    } else if (noPermissionErrors && !noSqlErrors) {
                        return "Не все элементы были удалены, т.к. возникли ошибки SQL!";
                    } else if (!noPermissionErrors) {
                        return "Не все элементы были удалены, т.к. вы не являетесь владельцем некоторых элементов!";
                    } else {
                        return "Коллекция очищена!";
                    }
                } else {
                    return "Вы не являетесь владельцем ни одного из элементов, поэтому коллекция не очищена!";
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
                    return "Коллекция очищена.";
                } else {
                    return "Не все элементы были удалены, т.к. возникли ошибки SQL!";
                }
            }
        }
        return "Коллекция пуста, нечего чистить!";
    }

    @Override
    public String description() {
        return "Очищает коллекцию." + syntax();
    }

    @Override
    public String syntax() {
        return " Синтаксис: clear";
    }
}
