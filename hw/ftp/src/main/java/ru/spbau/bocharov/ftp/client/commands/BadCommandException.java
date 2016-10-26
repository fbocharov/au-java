package ru.spbau.bocharov.ftp.client.commands;

public class BadCommandException extends CommandFactoryException {

    public BadCommandException(String msg) {
        super(msg);
    }
}
