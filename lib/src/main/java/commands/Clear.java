package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.ArrayList;
import java.util.Date;
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
                    final boolean[] noErrors = {true};
                    collection.stream().filter(x -> x.getUser().getName().equals(user.getName())).forEach(x -> {
                        if (dbUnit.removeProductFromDB(x)) {
                            collection.remove(x);
                            if (collection.stream().filter(y -> y.getManufacturer().equals(x.getManufacturer())).count() == 1) {
                                organizations.remove(x.getManufacturer());
                            }
                        } else {
                            noErrors[0] =false;
                        }
                    });
                    if (noErrors[0]) {
                        return "Коллекция очищена.";
                    } else {
                        return "Не все элементы были удалены, т.к. возникли ошибки SQL!";
                    }
                } else {
                    return "Вы не являетесь владельцем ни одного из элементов, поэтому коллекция не очищена!";
                }
            } else {
                final boolean[] noErrors = {true};
                collection.stream().forEach(x -> {
                    if (dbUnit.removeProductFromDB(x)) {
                        collection.remove(x);
                        if (collection.stream().filter(y -> y.getManufacturer().equals(x.getManufacturer())).count() == 1) {
                            organizations.remove(x.getManufacturer());
                        }
                    } else {
                        noErrors[0] = false;
                    }
                });
                if (noErrors[0]) {
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
