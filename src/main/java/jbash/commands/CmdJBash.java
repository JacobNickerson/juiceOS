package jbash.commands;

import jbash.environment.JKernel;
import jbash.environment.JProcess;
import jbash.parser.JBParserException;

import java.util.*;

import static jbash.parser.JBashParser.parseCommand;

class CmdJBash extends Command {
    CmdJBash(String name, JProcess parent) { super(name, parent); }

    private HashMap<String, String> aliases = new HashMap<>();
    private final Set<String> builtIns = Set.of(
            "alias", "cd", "echo", "pwd"
    );

    @Override
    public String getHelp() {
        return "Usage: " ;
    }

    @Override
    public int execute(List<String> argv) {
        var ENV = parent.env;
        var OS = JKernel.getInstance();
        Scanner userIn = new Scanner(System.in);
        while (true) {
            // Prompt
            System.out.print("[jbash] " + ENV.get("PWD") + " " + (ENV.get("?").equals("0") ? ENV.get("PS1") : "× "));

            ArrayList<String> tokens;
            try { tokens = parseCommand(userIn.nextLine()); }
            catch (JBParserException e) {
                // Parsing failed: let the user know what went wrong
                parent.send(2, e.getMessage()+"\n");
                parent.fdFlush(2);
                parent.fdFlush(2);
                continue;
            }
            if (tokens.isEmpty()) {
                ENV.set("?", "0");
                continue;
            }

            // Gather command name
            var cmdName = tokens.getFirst();

            // TODO: Fix Aliasing
            // Aliases allow for an entire command with args to be aliased to a keyword
            // I think for this we have to parse the alias as well and add any potential args
            // from the alias to the command args
//            // Attempt to look for aliases
//            int maxRecursion = 100;  // picked arbitrarily at random
//            while (maxRecursion-- > 0 && aliases.containsKey(cmdName)) {
//                cmdName = aliases.get(cmdName);
//            }
            if (cmdName.equals("exit")) { break; }

            // Run/Fork the command
            int returnCode;
            if (builtIns.contains(cmdName)) {
                returnCode = OS.exec(cmdName, tokens.subList(1, tokens.size()));
            } else {
                returnCode = OS.fork(cmdName, tokens.subList(1, tokens.size()));
            }

            ENV.set("?", Integer.toString(returnCode));
        }

        return 0;
    }
}
