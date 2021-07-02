package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveById extends Command {
    private Long id;

    public RemoveById(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        tag = interpreter.getTag();
        try {
            if (!arg.matches("\\s*[^\\s]+\\s*")) {
                throw new NumberFormatException();
            } else {
                Matcher m = Pattern.compile("[^\\s]+").matcher(arg);
                if (m.find()) {
                    id = Long.parseLong(m.group());
                }
                if (id <= 0) {
                    throw new NumberFormatException();
                }
            }
        } catch (NumberFormatException e) {
            content = getStringFromBundle("removeError");
            return false;
        }
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        Optional<Product> optional = collection.stream().filter(x -> x.getId().equals(id)).findFirst();
        if (optional.isPresent()) {
            if (user.getName().equals("admin") || optional.get().getUser().getName().equals(user.getName())) {
                if (dbUnit.removeProductFromDB(optional.get())) {
                    collection.remove(optional.get());
                    if (collection.stream().filter(x -> x.getManufacturer().equals(optional.get().getManufacturer())).count() == 1) {
                        organizations.remove(optional.get().getManufacturer());
                    }
                    return getStringFromBundle("removeSuccess1") + id + getStringFromBundle("removeSuccess2");
                } else {
                    return getStringFromBundle("removeError1") + id + getStringFromBundle("removeError2");
                }
            } else {
                return getStringFromBundle("removeError3") + id + getStringFromBundle("removeError4");
            }
        } else {
            return getStringFromBundle("removeError5") + id + ".";
        }
    }

    @Override
    public String description() {
        return getStringFromBundle("removeDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("removeSyntax");
    }
}
