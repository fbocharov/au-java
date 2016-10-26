package ru.spbau.bocharov.ftp.server.executors;

public class BadExecutorException extends ExecutorFactoryException {

    public BadExecutorException(String msg) {
        super(msg);
    }
}
