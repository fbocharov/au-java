package ru.spbau.bocharov.torrent.protocol;

public class RequestType {

    // client-server requests

    public static final byte LIST = 1;

    public static final byte UPLOAD = 2;

    public static final byte SOURCES = 3;

    public static final byte UPDATE = 4;

    // client-client requests

    public static final byte STAT = 101;

    public static final byte GET = 102;
}
