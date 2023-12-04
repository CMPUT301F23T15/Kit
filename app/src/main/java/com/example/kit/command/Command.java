package com.example.kit.command;

/**
 * Command design pattern implementation. Provides a structure for commands that can be done and
 * undone, with a flag method to represent its reversible status.
 */
public abstract class Command {

    public abstract void execute();

    public abstract void unexecute();

    /**
     * Is the command implementation's execution permanent?
     * Default is false, irreversible.
     * @return Reversibility of the command. False by default.
     */
    public boolean isReversible(){
        return false;
    }
}
