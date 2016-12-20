package ru.spbau.bocharov.serverbench.client.ui;

import ru.spbau.bocharov.serverbench.client.benchmark.BenchmarkRunner;

import javax.swing.*;

public class MainWindow extends JFrame {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    private final BenchmarkRunner model;

    MainWindow(BenchmarkRunner benchmark) {
        model = benchmark;

        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
    }
}
