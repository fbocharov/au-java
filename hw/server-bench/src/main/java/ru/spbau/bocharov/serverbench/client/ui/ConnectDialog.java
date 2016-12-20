package ru.spbau.bocharov.serverbench.client.ui;

import ru.spbau.bocharov.serverbench.client.benchmark.BenchmarkRunner;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_END;

public class ConnectDialog extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    public ConnectDialog() {
        setTitle("Benchmark 1.0");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel commonPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new SpringLayout());

        JLabel addressLabel = new JLabel("Address", JLabel.TRAILING);
        fieldsPanel.add(addressLabel);
        JTextField addressField = new JTextField(10);
        addressLabel.setLabelFor(addressField);
        fieldsPanel.add(addressField);

        JLabel portLabel = new JLabel("Port", JLabel.TRAILING);
        fieldsPanel.add(portLabel);
        JTextField portField = new JTextField(10);
        portLabel.setLabelFor(portField);
        fieldsPanel.add(portField);

        SpringUtilities.makeCompactGrid(fieldsPanel,2, 2,
                6, 6,
                6, 6);

        commonPanel.add(fieldsPanel, CENTER);
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> {
            String address = addressField.getText();
            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(ConnectDialog.this, "Address field is empty",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int port;
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ConnectDialog.this, "Port should be a number",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BenchmarkRunner benchmark = new BenchmarkRunner(address, port);
            setVisible(false);
            new MainWindow(benchmark).setVisible(true);
        });
        commonPanel.add(connectButton, PAGE_END);
        add(commonPanel);

        pack();
    }
}
