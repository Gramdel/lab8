package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class Command implements Serializable {
    protected String tag;

    protected User user;

    protected String content;

    public Command(User user) {
        this.user = user;
    }

    public abstract boolean prepare(String arg, boolean isInteractive, Interpreter interpreter);

    public String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        return "Команда успешно выполнена!";
    }

    public abstract String description();

    public abstract String syntax();

    public String getContent() {
        return content;
    }

    protected String getStringFromBundle(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.Language", Locale.forLanguageTag(tag));
        return new String(bundle.getString(key).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
