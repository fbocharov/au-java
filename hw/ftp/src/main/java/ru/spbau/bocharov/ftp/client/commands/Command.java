package ru.spbau.bocharov.ftp.client.commands;

import java.io.IOException;
import java.net.Socket;

public interface Command {

    void execute(Socket socket, String... args) throws IOException;
}
