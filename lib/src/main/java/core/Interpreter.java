package core;

import commands.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Stack;

public abstract class Interpreter {
    protected final HashMap<String, Command> commands = new HashMap<>();
    protected final Stack<String> history = new Stack<>();
    protected String content;
    protected String tag;

    public Interpreter(User user) {
        commands.put("add", new Add(user));
        commands.put("clear", new Clear(user));
        commands.put("exit", new Exit(user));
        commands.put("history", new History(user));
        commands.put("help", new Help(user));
        commands.put("execute_script", new ExecuteScript(user));
        commands.put("show", new Show(user));
        commands.put("remove_by_id", new RemoveById(user));
        commands.put("update", new Update(user));
        commands.put("remove_any_by_uom", new RemoveByUOM(user));
        commands.put("add_if_max", new AddIfMax(user));
        commands.put("remove_greater", new RemoveGreater(user));
    }

    public abstract void fromStream(InputStream stream, boolean isInteractive);

    protected void addToHistory(String com) {
        history.add(com);
        if (history.size() > 7) history.removeElementAt(0);
    }

    public Stack<String> getHistory() {
        return history;
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }

    public String getContent() {
        return content;
    }

    public void setTag(String tag) {
        this.tag = tag;
        commands.forEach((key, value) -> value.setTag(tag));
    }

    public String getTag() {
        return tag;
    }
}