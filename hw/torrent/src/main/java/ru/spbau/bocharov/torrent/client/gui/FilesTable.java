package ru.spbau.bocharov.torrent.client.gui;

import ru.spbau.bocharov.torrent.common.FileInfo;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class FilesTable extends JTable {

    private static final String[] COLUMNS = new String[] { "ID", "Name", "Size" };

    private final ModifiableModel model = new ModifiableModel();

    FilesTable(int width) {
        setModel(model);

        getColumnModel().getColumn(0).setPreferredWidth(2 * width / 10);
        getColumnModel().getColumn(1).setPreferredWidth(6 * width / 10);
        getColumnModel().getColumn(2).setPreferredWidth(2 * width / 10);
    }

    FileInfo getFile(int row) {
        if (row >= 0 && row < model.rows.size()) {
            return model.rows.get(row);
        }
        return null;
    }

    void setRows(List<FileInfo> rows) {
        model.rows = rows;
        model.fireTableDataChanged();
    }

    private static class ModifiableModel extends AbstractTableModel {

        private List<FileInfo> rows = new LinkedList<>();

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
            FileInfo row = rows.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return row.getFileId();
                case 1:
                    return row.getFileName();
                case 2:
                    return row.getSize();
            }
            return 0;
        }
    }

}
