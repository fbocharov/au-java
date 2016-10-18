package ru.spbau.bocharov.ftp.client.commands;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandFactoryTest {

    @Test
    public void shouldCreateCommands() throws Exception {
        assertTrue(CommandFactory.getInstance().createCommand("list") instanceof ListCommand);
        assertTrue(CommandFactory.getInstance().createCommand("get") instanceof GetCommand);
    }
}