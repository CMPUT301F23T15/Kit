package com.example.kit.command;

import java.util.LinkedList;

/**
 * Manages {@link Command}s, executing them and offering the undo and redo functionality for
 * commands that support it. Manages a list of commands that can be undone or redone.
 */
public class CommandManager {
    private final LinkedList<Command> historyList;
    private final LinkedList<Command> redoList;

    /**
     * Execute the given command, add it to the command history list if it is reversible. Clears the
     * history of commands if it is not reversible to prevent undo-ing commands from other contexts.
     * Clear the redo list, for simplicity.
     * @param command Command to be executed.
     */
    public void executeCommand(Command command) {
        command.execute();

        // Add to history list if it is reversible
        // Clear history list if it is not, so we cannot undo old commands we don't know about
        // from the context that the last command was executed in.
        if (command.isReversible()) {
            historyList.addFirst(command);
        } else {
            historyList.clear();
        }

        // Cannot redo things after adding new command for simplicity
        if (redoList.size() > 0) {
            redoList.clear();
        }
    }

    /**
     * Undoes one command from the history list, adding it to the redo list.
     */
    public void undo() {
        if (historyList.size() > 0) {
            Command command = historyList.removeFirst();
            command.unexecute();
            redoList.addFirst(command);
        }
    }

    /**
     * Redoes one command from the redo list and adds it to the history list.
     */
    public void redo() {
        if (redoList.size() > 0) {
            Command command = redoList.removeFirst();
            command.execute();
            historyList.add(command);
        }
    }

    // Singleton Instantiation
    private static CommandManager instance;

    /**
     * Singleton constructor, initializes empty history and redo lists.
     */
    private CommandManager() {
        historyList = new LinkedList<>();
        redoList = new LinkedList<>();
    }

    /**
     * Access the CommandManager instance.
     * @return Instance of the CommandManager.
     */
    public static synchronized CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }
}
