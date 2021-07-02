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
            content = getStringFromBundle("exitError");
            return false;
        } else {
            System.exit(0);
        }
        return true;
    }

    @Override
    public String description() {
        return getStringFromBundle("exitDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("exitSyntax");
    }
}
