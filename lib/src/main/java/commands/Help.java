package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.util.*;

public class Help extends Command {
    private HashMap<String, Command> commands;

    public Help(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        tag = interpreter.getTag();
        if (!arg.matches("\\s*")) {
            content = getStringFromBundle("helpError");
            return false;
        }
        this.commands = interpreter.getCommands();
        return true;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        StringBuilder s = new StringBuilder();
        commands.forEach((commandName, command) -> s.append("\n").append(commandName).append(" - ").append(command.description()));
        return getStringFromBundle("helpSuccess") + s;
    }

    @Override
    public String description() {
        return getStringFromBundle("helpDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("helpSyntax");
    }
}
