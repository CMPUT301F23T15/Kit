package com.example.kit.command;

public abstract class Command {

    public abstract void execute();

    public abstract void unexecute();

    public boolean isReversible(){
        return false;
    }
}
