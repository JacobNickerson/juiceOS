package jbash.commands;

import jbash.environment.JKernel;
import jbash.environment.JProcess;
import jbash.parser.JBParserException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static jbash.parser.JBashParser.parseCommand;

class CmdJBash extends Command {
    CmdJBash(String name, JProcess parent) { super(name, parent); }

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
            if (cmdName.equals("exit")) { break; }

            // Fork and run
            int returnCode = OS.fork(cmdName, tokens.subList(1, tokens.size()));
            ENV.set("?", Integer.toString(returnCode));
        }

        return 0;
    }
}
