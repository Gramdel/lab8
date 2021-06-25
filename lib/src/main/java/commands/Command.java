package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

public abstract class Command implements Serializable {
    protected User user;

    public Command(User user) {
        this.user = user;
    }

    public abstract boolean prepare(String arg, boolean isInteractive, Interpreter interpreter);

    public String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        return "Команда успешно выполнена!";
    }

    public abstract String description();

    public abstract String syntax();
}
