package ru.spbau.bocharov.ftp.client.commands;

public class UnknownCommandException extends CommandFactoryException {

    public UnknownCommandException(String msg) {
        super(msg);
    }
}
