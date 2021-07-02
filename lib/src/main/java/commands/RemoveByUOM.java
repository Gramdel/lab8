package commands;

import collection.Organization;
import collection.Product;
import collection.UnitOfMeasure;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveByUOM extends Command {
    private UnitOfMeasure unitOfMeasure;

    public RemoveByUOM(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        tag = interpreter.getTag();
        try {
            if (!arg.matches("\\s*[^\\s]+\\s*")) {
                throw new IllegalArgumentException();
            } else {
                Matcher m = Pattern.compile("[^\\s]+").matcher(arg);
                if (m.find()) {
                    unitOfMeasure = UnitOfMeasure.fromString(m.group());
                }
            }
        } catch (IllegalArgumentException e) {
            content = getStringFromBundle("removeUOMError") + UnitOfMeasure.valueList() + ".";
            return false;
        }
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        long count = collection.stream().filter(x -> x.getUnitOfMeasure().equals(unitOfMeasure)).count();
        if (count > 0) {
            Optional<Product> optional = collection.stream().filter(x -> x.getUnitOfMeasure().equals(unitOfMeasure)).filter(x -> user.getName().equals("admin") || x.getUser().getName().equals(user.getName())).findAny();
            if (optional.isPresent()) {
                if (dbUnit.removeProductFromDB(optional.get())) {
                    collection.remove(optional.get());
                    if (collection.stream().filter(x -> x.getManufacturer().equals(optional.get().getManufacturer())).count() == 1) {
                        organizations.remove(optional.get().getManufacturer());
                    }
                    return getStringFromBundle("removeUOMSuccess1") + unitOfMeasure + getStringFromBundle("removeUOMSuccess2");
                } else {
                    return getStringFromBundle("removeUOMError1") + unitOfMeasure + getStringFromBundle("removeUOMError2");
                }
            } else {
                return getStringFromBundle("removeUOMError3") + count + getStringFromBundle("removeUOMError4") + unitOfMeasure + getStringFromBundle("removeUOMError5");
            }
        } else {
            return getStringFromBundle("removeUOMError6") + unitOfMeasure + "!";
        }
    }

    @Override
    public String description() {
        return getStringFromBundle("removeUOMDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("removeUOMSyntax") + UnitOfMeasure.valueList() + ".";
    }
}
