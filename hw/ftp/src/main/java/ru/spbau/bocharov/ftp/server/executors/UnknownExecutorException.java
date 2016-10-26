package ru.spbau.bocharov.ftp.server.executors;

public class UnknownExecutorException extends ExecutorFactoryException {

    public UnknownExecutorException(String msg) {
        super(msg);
    }
}
