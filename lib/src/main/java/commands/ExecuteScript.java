package commands;

import collection.Organization;
import collection.Product;
import core.DBUnit;
import core.Interpreter;
import core.User;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ExecuteScript extends Command {
    private final Stack<String> scripts = new Stack<>();
    private String arg;

    public ExecuteScript(User user) {
        super(user);
    }

    @Override
    public boolean prepare(String arg, boolean isInteractive, Interpreter interpreter) {
        tag = interpreter.getTag();
        if (!Files.exists(Paths.get(arg))) {
            content = getStringFromBundle("execMsg") + arg + getStringFromBundle("execError");
        } else if (Files.isDirectory(Paths.get(arg))) {
            content = getStringFromBundle("execMsg") + arg + getStringFromBundle("execError1");
        } else if (!Files.isRegularFile(Paths.get(arg))) {
            content = getStringFromBundle("execMsg") + arg + getStringFromBundle("execError2");
        } else if (!Files.isReadable(Paths.get(arg))) {
            content = getStringFromBundle("execMsg") + arg + getStringFromBundle("execError3");
        } else if (scripts.contains(arg)) {
            content = getStringFromBundle("execMsg") + arg + getStringFromBundle("execError4");
        } else {
            try {
                scripts.push(arg);
                interpreter.fromStream(new BufferedInputStream(new FileInputStream(arg)), false);
                this.arg = arg;
                scripts.remove(arg);
                return true;
            } catch (FileNotFoundException e) {
                content = getStringFromBundle("execMsg") + arg + getStringFromBundle("execError");
            }
        }
        return false;
    }

    @Override
    public synchronized String execute(LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit) {
        return getStringFromBundle("execMsg") + arg + getStringFromBundle("execSuccess");
    }

    @Override
    public String description() {
        return getStringFromBundle("execDesc") + syntax();
    }

    @Override
    public String syntax() {
        return getStringFromBundle("execSyntax");
    }
}
