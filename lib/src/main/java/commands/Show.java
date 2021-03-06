package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.*;

public class Show extends Command {
    public Show(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        tag = interpreter.getTag();
        if (!arg.matches("\\s*")) {
            //System.out.println("У команды show не может быть аргументов!");
            return false;
        }
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        if (collection.size() > 0) {
            StringBuilder msg = new StringBuilder();
            collection.stream().min(Product.byIdComparator).ifPresent(msg::append);
            collection.stream().sorted(Product.byIdComparator).skip(1).forEach(p -> msg.append(",\n").append(p));
            return msg.toString();
        } else {
            return "0";
        }
    }

    @Override
    public String description() {
        return getStringFromBundle("showDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("showSyntax");
    }
}
