package ru.spbau.bocharov.torrent.client.gui;

import ru.spbau.bocharov.torrent.client.DownloadStateListener;
import ru.spbau.bocharov.torrent.client.TorrentClient;
import ru.spbau.bocharov.torrent.common.FileInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MainWindow extends JFrame implements DownloadStateListener {

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 600;

    private final DownloadsTable downloadsTable = new DownloadsTable(FRAME_WIDTH);
    private final FilesTable filesTable = new FilesTable(FRAME_WIDTH);
    private final TorrentClient model;

    public MainWindow(TorrentClient client) {
        model = client;

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setTitle("Torrent 1.0");
        setResizable(false);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Files", createFilesPanel());
        tabs.add("Downloads", createDownloadsPanel());

        add(tabs);
    }

    @Override
    public void startDownload(FileInfo file) {
        downloadsTable.addFile(file);
    }

    @Override
    public void updateState(int fileId, double percentage) {
        downloadsTable.updateFile(fileId, (int) (percentage * 100));
    }

    private JPanel createFilesPanel() {
        JPanel filesPanel = new JPanel(new BorderLayout());

        filesPanel.add(new JScrollPane(filesTable), BorderLayout.CENTER);
        filesTable.setFillsViewportHeight(true);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(createGetButton());
        buttonsPanel.add(createUploadButton());
        buttonsPanel.add(createRefreshButton());
        add(buttonsPanel, BorderLayout.PAGE_END);

        return filesPanel;
    }

    private JButton createGetButton() {
        JButton button = new JButton("Get");
        button.addActionListener(e -> {
            int row = filesTable.getSelectedRow();
            FileInfo file = filesTable.getFile(row);
            if (file == null) {
                JOptionPane.showMessageDialog(filesTable, "Unknown row selected", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    model.get(file.getFileId());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(filesTable, "Error occured: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return button;
    }

    private JButton createUploadButton() {
        JButton button = new JButton("Upload");
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File(model.getFolder()));
            int ret = chooser.showOpenDialog(filesTable);
            if (ret == JFileChooser.APPROVE_OPTION) {
                String name = chooser.getSelectedFile().getName();
                try {
                    model.upload(name);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(filesTable, "Error occured: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return button;
    }

    private JButton createRefreshButton() {
        JButton button = new JButton("Refresh");
        button.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            try {
                List<FileInfo> files = model.list();
                filesTable.setRows(files);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(filesTable, "Error occured: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }));
        return button;
    }

    private JPanel createDownloadsPanel() {
        JPanel downloadsPanel = new JPanel(new BorderLayout());

        downloadsPanel.add(new JScrollPane(downloadsTable), BorderLayout.CENTER);
        downloadsTable.setFillsViewportHeight(true);

        return downloadsPanel;
    }
}
