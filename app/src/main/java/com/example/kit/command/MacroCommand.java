package com.example.kit.command;

import java.util.ArrayList;
import java.util.Collections;

public class MacroCommand extends Command {

    private final ArrayList<Command> commands;

    public MacroCommand() {
        commands = new ArrayList<>();
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }

    @Override
    public void unexecute() {
        ArrayList<Command> reversedCommands = new ArrayList<>(commands);
        Collections.reverse(reversedCommands);
        for (Command command : reversedCommands) {
            command.unexecute();
        }
    }

    @Override
    public boolean isReversible() {
        return true;
    }
}
