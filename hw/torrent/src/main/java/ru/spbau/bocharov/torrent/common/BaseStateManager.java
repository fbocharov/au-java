package ru.spbau.bocharov.torrent.common;

import java.io.*;

public class BaseStateManager {

    private String stateFilePath;

    protected BaseStateManager(String path) {
        stateFilePath = path;
    }

    protected  <T> T loadState() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(stateFilePath))) {
            return (T) in.readObject();
        } catch (IOException e) {
            System.err.println(String.format("failed to load state from %s: %s", stateFilePath, e.getMessage()));
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            System.err.println(String.format("failed to load state from %s: %s", stateFilePath, e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    protected void saveState(Object state) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(stateFilePath))) {
            out.writeObject(state);
        } catch (IOException e) {
            System.err.println(String.format("failed to save state to %s: %s", stateFilePath, e.getMessage()));
            throw new UncheckedIOException(e);
        }
    }

}
