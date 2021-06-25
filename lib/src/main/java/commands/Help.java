package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Help extends Command {
    private HashMap<String, Command> commands;

    public Help(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        if (!arg.matches("\\s*")) {
            System.out.println("У команды help не может быть аргументов!");
            return false;
        }
        this.commands = interpreter.getCommands();
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        StringBuilder s = new StringBuilder();
        commands.forEach((commandName, command) -> s.append("\n\t").append(commandName).append(" - ").append(command.description()));
        return "Список допустимых команд:" + s;
    }

    @Override
    public String description() {
        return "Выводит справку по доступным коммандам." + syntax();
    }

    @Override
    public String syntax() {
        return " Синтаксис: help";
    }
}
