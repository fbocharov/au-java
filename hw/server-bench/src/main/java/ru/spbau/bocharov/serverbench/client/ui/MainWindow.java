package ru.spbau.bocharov.serverbench.client.ui;

import ru.spbau.bocharov.serverbench.client.benchmark.BenchmarkConfiguration;
import ru.spbau.bocharov.serverbench.client.benchmark.BenchmarkResult;
import ru.spbau.bocharov.serverbench.client.benchmark.BenchmarkRunner;
import ru.spbau.bocharov.serverbench.client.benchmark.ParameterRange;
import ru.spbau.bocharov.serverbench.common.ServerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class MainWindow extends JFrame {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    private final BenchmarkRunner model;
    private final JComboBox<ServerDescriptor> serverDescriptionCombobox = createComboBox();
    private final ParameterPanel arraySizePanel = new ParameterPanel("Element count (N)");
    private final ParameterPanel clientCountPanel = new ParameterPanel("Client count (M)   ");
    private final ParameterPanel deltaPanel = new ParameterPanel("Time delta (ms)    ");
    private final ParameterPanel requestCountPanel = new ParameterPanel("Request count (X)");


    MainWindow(BenchmarkRunner benchmark) {
        model = benchmark;

        setTitle("Benchmark 1.0");
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel commonPanel = new JPanel(new BorderLayout());

        JPanel controlPanel = new JPanel(new GridLayout(6, 1));
        controlPanel.add(serverDescriptionCombobox);
        controlPanel.add(arraySizePanel);
        controlPanel.add(clientCountPanel);
        controlPanel.add(deltaPanel);
        controlPanel.add(requestCountPanel);

        JButton runButton = new JButton("Run");
        runButton.addActionListener(e -> {
            ParameterRange arraySizeRange = arraySizePanel.getRange();
            if (!checkRange(arraySizeRange, "Array size")) {
                return;
            }

            ParameterRange clientsRange = clientCountPanel.getRange();
            if (!checkRange(clientsRange, "Clients count")) {
                return;
            }

            ParameterRange deltaRange = deltaPanel.getRange();
            if (!checkRange(deltaRange, "Time delta")) {
                return;
            }

            ParameterRange requestsRange = requestCountPanel.getRange();
            if (!checkRange(requestsRange, "Request count")) {
                return;
            }

            new Thread(() -> {
                Iterator<BenchmarkConfiguration> it = BenchmarkConfiguration.makeIterator(
                        ((ServerDescriptor) serverDescriptionCombobox.getSelectedItem()).getType(),
                        clientsRange, arraySizeRange, requestsRange, deltaRange);

                while (it.hasNext()) {
                    BenchmarkConfiguration configuration = it.next();
                    try {
                        BenchmarkResult result = model.run(configuration);
                        // TODO: print result
                    } catch (InterruptedException ex) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Error while running benchmark: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
                runButton.setEnabled(true);
            }).start();
            runButton.setEnabled(false);
        });
        controlPanel.add(runButton);
        commonPanel.add(controlPanel, BorderLayout.PAGE_START);
        add(commonPanel);
    }

    private boolean checkRange(ParameterRange range, String prefix) {
        if (range.to == 0) {
            JOptionPane.showMessageDialog(MainWindow.this,
                    prefix + " step field should be > 0", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (range.step == 0) {
            JOptionPane.showMessageDialog(MainWindow.this,
                    prefix + " step field should be > 0", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private static JComboBox<ServerDescriptor> createComboBox() {
        JComboBox<ServerDescriptor> comboBox = new JComboBox<>();

        comboBox.addItem(new ServerDescriptor(ServerType.SINGLE_THREAD_UDP_SERVER,
                "UDP server with single thread for all clients"));
        comboBox.addItem(new ServerDescriptor(ServerType.THREAD_POOL_UDP_SERVER,
                "UDP server with fixed thread pool for all clients"));

        comboBox.addItem(new ServerDescriptor(ServerType.SINGLE_THREAD_TCP_SERVER,
                "TCP server with single thread for all clients"));
        comboBox.addItem(new ServerDescriptor(ServerType.THREAD_PER_CLIENT_TCP_SERVER,
                "TCP server with new thread for new client"));
        comboBox.addItem(new ServerDescriptor(ServerType.THREAD_CACHING_TCP_SERVER,
                "TCP server with caching thread pool for all clients"));
        comboBox.addItem(new ServerDescriptor(ServerType.POLLING_TCP_SERVER,
                "TCP server with non-blocking IO and thread pool"));
        comboBox.addItem(new ServerDescriptor(ServerType.ASYNC_TCP_SERVER,
                "TCP server with async non-bocking client handling"));

        return comboBox;
    }
}
