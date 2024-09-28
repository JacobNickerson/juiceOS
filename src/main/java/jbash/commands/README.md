# Programmer's Guide to Adding Commands
The process of adding a new command can be as simple as the following:
- Add new case to the CommandFactory, i.e: `case "foo" -> new CmdFoo(commandName)`
- Create the new class `CmdFoo` in the commands package, and have it extend `Command`.

A structure you can use for new commands is outlined here. You may copy-paste and fill in the blanks, if you like.
```java
package jbash.commands;

import java.util.List;

// TODO: Change command name
class CHANGEME extends Command {
    // TODO: Change constructor to command name, and add string version of command name to super()
    // I.e. CmdEcho(String name) { super("echo"); }
    CHANGEME(String name) { super(CHANGEME); }

    @Override
    public String getHelp() {
        // TODO: ADD USAGE
        // It can be helpful to provide users a description of how to use this command.
        // This is a place you can provide a brief description.
        // Otherwise, leave as blank (if you are a heathen)
        return "Usage: " + CHANGEME;
    }

    @Override
    public int execute(List<String> argv) {
        // TODO: ADD IMPLEMENTATION
        // This is where you'd provide any implementation for your command.
        // If the command results in an error, you will probably want to return a non-zero exit status,
        // so the calling process knows about the error.

        // Additionally, remember to use cmdPrintln and cmdErrln -- do NOT use System.out.println!
        return 0;
    }
}

```

# Important:
For the system to work properly, it is recommended to use the `cmdPrintln` and `cmdErrln` functions instead of `System.out.println`.
By using `System.out.println`, you are bypassing the shiny new IO system that JBash provides, and things may not work as you expect.