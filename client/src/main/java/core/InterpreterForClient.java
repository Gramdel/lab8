package core;

import commands.*;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterpreterForClient extends Interpreter {
    public InterpreterForClient(User user) {
        super(user);
    }

    public void fromStream(InputStream stream, boolean isInteractive) {
        Scanner in = new Scanner(stream);
        while (in.hasNext()) {
            String s = in.nextLine();
            if (!s.matches("\\s*")) {
                String com = "";
                String arg = "";
                Matcher m = Pattern.compile("[^\\s]+").matcher(s);
                if (m.find()) {
                    com = m.group();
                    s = m.replaceFirst("");
                }
                m = Pattern.compile("[\\s]+").matcher(s);
                if (m.find()) {
                    arg = m.replaceFirst("");
                }
                addToHistory(com);
                Command command = commands.get(com);
                if (command != null) {
                    if (command.prepare(arg, isInteractive, this)) {
                        Client.sendCommandAndReceiveResult(command);
                    }
                } else {
                    System.out.println("Такой команды не существует! Список команд: help");
                }
            }
        }
    }
}
