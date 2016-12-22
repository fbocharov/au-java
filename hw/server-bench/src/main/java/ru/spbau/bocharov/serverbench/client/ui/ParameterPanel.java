package ru.spbau.bocharov.serverbench.client.ui;

import ru.spbau.bocharov.serverbench.client.benchmark.ParameterRange;

import javax.swing.*;

public class ParameterPanel extends JPanel {

    private final JTextField fromTextField = new JTextField("1", 6);
    private final JTextField toTextField = new JTextField("100", 6);
    private final JTextField stepTextField = new JTextField("10", 6);

    ParameterPanel(String labelText) {
        JLabel label = new JLabel(labelText);

        add(label);
        add(fromTextField);
        add(toTextField);
        add(stepTextField);
    }

    ParameterRange getRange() {
        return new ParameterRange(
                Integer.valueOf(fromTextField.getText()),
                Integer.valueOf(toTextField.getText()),
                Integer.valueOf(stepTextField.getText()));
    }
}
