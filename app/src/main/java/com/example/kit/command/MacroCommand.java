package com.example.kit.command;

import java.util.ArrayList;
import java.util.Collections;

/**
 * {@link Command} that provides the function of executing multiple commands in sequence.
 */
public class MacroCommand extends Command {

    private final ArrayList<Command> commands;

    /**
     * Construct a macro command, with an empty list of commands.
     */
    public MacroCommand() {
        commands = new ArrayList<>();
    }

    /**
     * Add a {@link Command} to the macro that will be executed when the macro is executed.
     * @param command Command to be added to the macro.
     */
    public void addCommand(Command command) {
        commands.add(command);
    }

    /**
     * Execution method of the macro, executing all subcommands sequentially.
     */
    @Override
    public void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }

    /**
     * Reverses the commands executed in reverse sequential order.
     */
    @Override
    public void unexecute() {
        ArrayList<Command> reversedCommands = new ArrayList<>(commands);
        Collections.reverse(reversedCommands);
        for (Command command : reversedCommands) {
            command.unexecute();
        }
    }

    /**
     * If any command in the macro is not reversible, the entire macro is not reversible.
     * @return False if any subcommand is permanent, otherwise true.
     */
    @Override
    public boolean isReversible() {
        for (Command command : commands) {
            if (!command.isReversible()) return false;
        }
        return true;
    }
}
