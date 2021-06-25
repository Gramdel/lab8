package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

public class Info extends Command {
    public Info(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        if (!arg.matches("\\s*")) {
            System.out.println("У команды info не может быть аргументов!");
            return false;
        }
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        return "Тип коллекции:\n" + collection.getClass() + "\n" + "Дата инициализации коллекции:\n" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date) + "\n" + "Количество элементов коллекции:\n" + collection.size();
    }

    @Override
    public String description() {
        return "Выводит информацию о коллекции." + syntax();
    }

    @Override
    public String syntax() {
        return " Синтаксис: info";
    }
}
