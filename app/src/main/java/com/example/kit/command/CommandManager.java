package com.example.kit.command;

import java.util.LinkedList;

public class CommandManager {
    private LinkedList<Command> historyList;
    private LinkedList<Command> redoList;

    public void executeCommand(Command command) {
        command.execute();


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
