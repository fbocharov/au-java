package ru.spbau.bocharov.serverbench.client;

import ru.spbau.bocharov.serverbench.client.ui.ConnectDialog;

import javax.swing.*;

public class GUIClientMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ConnectDialog().setVisible(true);
        });
    }
}
