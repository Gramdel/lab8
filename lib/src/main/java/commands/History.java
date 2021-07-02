package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.*;

public class History extends Command {
    private Stack<String> history;

    public History(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        tag = interpreter.getTag();
        if (!arg.matches("\\s*")) {
            content = getStringFromBundle("historyError");
            return false;
        }
        history = interpreter.getHistory();
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        if (history.size() > 0) {
            StringBuilder s = new StringBuilder();
            history.forEach(x -> s.append("\n\t").append(x));
            return getStringFromBundle("historySuccess") + s;
        } else {
            return getStringFromBundle("historyEmpty");
        }
    }

    @Override
    public String description() {
        return getStringFromBundle("historyDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("historySyntax");
    }
}
