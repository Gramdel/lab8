package commands;

import core.Interpreter;
import core.User;

public class Exit extends Command {
    public Exit(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        if (!arg.matches("\\s*")) {
            System.out.println("У команды exit не может быть аргументов!");
            return false;
        } else {
            System.out.println("Комманда exit выполнена, программа завершает работу.");
            System.exit(0);
        }
        return true;
    }

    @Override
    public String description() {
        return "Прекращает работу программы (без сохранения коллекции в файл)." + syntax();
    }

    @Override
    public String syntax() {
        return " Синтаксис: exit";
    }
}
