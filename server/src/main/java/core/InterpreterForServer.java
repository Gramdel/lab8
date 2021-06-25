package core;

import collection.Organization;
import collection.Product;
import commands.*;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterpreterForServer extends Interpreter {
    private final LinkedHashSet<Product> collection;
    private final ArrayList<Organization> organizations;
    private final Date date;
    private final DBUnit dbUnit;

    public InterpreterForServer (LinkedHashSet<Product> collection, ArrayList<Organization> organizations, Date date, DBUnit dbUnit, User user) {
        super(user);
        this.collection = collection;
        this.organizations = organizations;
        this.date = date;
        this.dbUnit = dbUnit;
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
                        System.out.println(command.execute(collection, organizations, date, dbUnit));
                    }
                } else {
                    System.out.println("Такой команды не существует! Список команд: help");
                }
            }
        }
    }
}