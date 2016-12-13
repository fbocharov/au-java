package ru.spbau.bocharov.serverbench.server;

public class ServerException extends RuntimeException {

    public ServerException(String msg) {
        super(msg);
    }

    public ServerException(Exception e) {
        super(e);
    }
}
