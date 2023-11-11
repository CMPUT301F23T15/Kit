package com.example.kit.command;

import java.util.LinkedList;

public class CommandManager {
    private final LinkedList<Command> historyList;
    private final LinkedList<Command> redoList;

    public void executeCommand(Command command) {
        command.execute();

        // Add to history list if it is reversible
        if (command.isReversible()) {
            historyList.addFirst(command);
        // Clear history list if it is not, so we cannot undo old commands we don't know about
        // from the context that the last command was executed in.
        } else {
            historyList.clear();
        }

        // Cannot redo things after adding new command for simplicity
        if (redoList.size() > 0) {
            redoList.clear();
        }
    }

    public void undo() {
        if (historyList.size() > 0) {
            Command command = historyList.removeFirst();
            command.unexecute();
            redoList.addFirst(command);
        }
    }

    public void redo() {
        if (redoList.size() > 0) {
            Command command = redoList.removeFirst();
            command.execute();
            historyList.add(command);
        }
    }

    // Singleton Instantiation
    private static CommandManager instance;

    private CommandManager() {
        historyList = new LinkedList<>();
        redoList = new LinkedList<>();
    }

    public static synchronized CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }
}
