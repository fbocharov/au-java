package ru.spbau.bocharov.concurrency;

public class LightExecutionException extends RuntimeException {
    public LightExecutionException(Exception e) {
        super(e);
    }
}
