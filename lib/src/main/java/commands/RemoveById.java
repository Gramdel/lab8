package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveById extends Command {
    private Long id;

    public RemoveById(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        try {
            if (!arg.matches("\\s*[^\\s]+\\s*")) {
                throw new NumberFormatException();
            } else {
                Matcher m = Pattern.compile("[^\\s]+").matcher(arg);
                if (m.find()) {
                    id = Long.parseLong(m.group());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("У команды remove_by_id должен быть 1 аргумент - положительное целое число!");
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
                    return "Элемент с id " + id + " успешно удалён!";
                } else {
                    return "При удалении элемента с id " + id + " произошла ошибка SQL!";
                }
            } else {
                return "Вы не являетесь владельцем элемента с id " + id + ", поэтому у вас нет прав на его удаление!";
            }
        } else {
            return "Удаление невозможно, так как в коллекции нет элемента с id " + id + ".";
        }
    }

    @Override
    public String description() {
        return "Удаляет элемент из коллекции по его id." + syntax();
    }

    @Override
    public String syntax() {
        return " Синтаксис: remove_by_id id, где id - целое положительное число.";
    }
}
