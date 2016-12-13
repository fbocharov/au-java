package ru.spbau.bocharov.serverbench.common;

import java.io.IOException;

public class ProtocolException extends IOException {

    public ProtocolException(String msg) {
        super(msg);
    }
}
