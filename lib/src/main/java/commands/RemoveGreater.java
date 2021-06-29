package commands;

import collection.Organization;
import collection.Product;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import core.Creator;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveGreater extends Command {
    private Product product;

    public RemoveGreater(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        Product product = null;
        try {
            if (isInteractive) {
                if (!arg.matches("\\s*")) {
                    throw new IllegalArgumentException("У команды remove_greater не может быть аргументов!");
                }
            } else {
                if (!arg.matches("\\s*\\{.*}\\s*")) {
                    throw new IllegalArgumentException("У команды remove_greater должен быть 1 аргумент: JSON-строка!");
                } else {
                    Matcher m = Pattern.compile("\\{.*}").matcher(arg);
                    if (m.find()) {
                        product = new Gson().fromJson(m.group(), Product.class);
                    }
                }
            }
            product = Creator.createProduct(product, isInteractive);
            if (product == null) {
                content = "Команда remove_greater не выполнена, т.к. не получилось создать продукт!";
                return false;
            }
        } catch (JsonSyntaxException | NumberFormatException e) {
            content = "Ошибка в синтаксисе JSON-строки! "+e.getMessage();
            return false;
        } catch (IllegalArgumentException e) {
            content = e.getMessage();
            return false;
        }
        this.product = product;
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        int prevSize = collection.size();
        if (prevSize == 0) {
            return "Т.к. коллекция пуста, невозможно удалить из неё элементы, цена которых больше цены данного.";
        } else {
            StringBuilder s = new StringBuilder();
            for (Iterator<Product> iter = collection.iterator(); iter.hasNext(); ) {
                Product product = iter.next();
                if (this.product.getPrice() < product.getPrice()) {
                    if (user.getName().equals("admin") || product.getUser().getName().equals(user.getName())) {
                        if (dbUnit.removeProductFromDB(product)) {
                            if (collection.stream().filter(x -> x.getManufacturer().equals(product.getManufacturer())).count() == 1) {
                                organizations.remove(product.getManufacturer());
                            }
                            iter.remove();
                        } else {
                            s.append("При удалении элемента с id ").append(product.getId()).append(" произошла ошибка SQL!\n");
                        }
                    } else {
                        s.append("Вы не являетесь владельцем элемента с id ").append(product.getId()).append(", поэтому у вас нет прав на его удаление!\n");
                    }
                }
            }
            if (prevSize > collection.size()) {
                if (s.toString().isEmpty()) {
                    return "0";
                } else {
                    return s + "Не все элементы, цена которых больше цены данного, были удалены!";
                }
            } else {
                return s + "Ничего не удалено, т.к. в коллекции нет элементов, цена которых больше цены данного, или возникли ошибки!";
            }
        }
    }

    @Override
    public String description() {
        return "Удаляет из коллеккции все элементы, цена которых больше цены данного." + syntax();
    }

    @Override
    public String syntax() {
        return " Синтаксис: remove_greater \n\t(В скриптах - remove_greater {element}";
    }
}
