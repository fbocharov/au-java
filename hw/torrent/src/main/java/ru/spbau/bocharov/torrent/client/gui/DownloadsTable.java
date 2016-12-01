package ru.spbau.bocharov.torrent.client.gui;

import lombok.Getter;
import lombok.Setter;
import ru.spbau.bocharov.torrent.client.DownloadStateListener;
import ru.spbau.bocharov.torrent.common.FileInfo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class DownloadsTable extends JTable {

    private static final String[] COLUMNS = new String[] { "ID", "Name", "Size", "Progress" };

    private final ModifiableModel model = new ModifiableModel();

    DownloadsTable(int width) {
        setModel(model);

        getColumnModel().getColumn(0).setPreferredWidth(width / 10);
        getColumnModel().getColumn(1).setPreferredWidth(width / 2);
        getColumnModel().getColumn(2).setPreferredWidth(width / 10);
        getColumnModel().getColumn(3).setPreferredWidth(3 * width / 10);

        getColumn(COLUMNS[3]).setCellRenderer(new ProgressCellRender());
    }

    void addFile(FileInfo file) {
        model.addFile(file);
    }

    void updateFile(int fileId, int progress) {
        model.updateStatus(fileId, progress);
        model.fireTableDataChanged();
    }

    private static class ProgressCellRender extends JProgressBar implements TableCellRenderer {

        ProgressCellRender() {
            setMinimum(0);
            setMaximum(100);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setValue((int) value);
            return this;
        }
    }

    private static class Row {

        private final FileInfo info;
        @Getter
        @Setter
        private int status;

        Row(FileInfo fileInfo) {
            info = fileInfo;
            status = 0;
        }

        int getId() {
            return info.getFileId();
        }

        String getName() {
            return info.getFileName();
        }

        long getSize() {
            return info.getSize();
        }

    }

    private static class ModifiableModel extends AbstractTableModel {

        private final List<Row> rows = new LinkedList<>();
        private Map<Integer, Integer> fileToRow = new HashMap<>();

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Row row = rows.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return row.getId();
                case 1:
                    return row.getName();
                case 2:
                    return row.getSize();
                case 3:
                    return row.getStatus();
            }
            return 0;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Row row = rows.get(rowIndex);
            switch (columnIndex) {
                case 3:
                    if (aValue instanceof Integer) {
                        row.setStatus((int) aValue);
                    }
                    break;
            }
        }

        void addFile(FileInfo file) {
            Row row = new Row(file);
            fileToRow.put(file.getFileId(), rows.size());
            rows.add(row);
            fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
        }

        void updateStatus(int fileId, int progress) {
            int index = fileToRow.get(fileId);
            Row row = rows.get(index);
            if (row != null) {
                setValueAt(progress, index, 3);
                fireTableCellUpdated(index, 3);
            }
        }
    }
}
