package org.example.bronze.ui;

import org.example.bronze.ui.util.Refreshable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public abstract class Tab extends JPanel implements Refreshable
{
    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private final String[] columns;

    public Tab(String[] columns)
    {
        this.columns = columns;
    }

    protected JScrollPane createTable()
    {
        tableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false; // disable direct editing
            }
        };

        table = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);
        return new JScrollPane(table);
    }

    protected void updateData(Object[][] data)
    {
        if (data != null)
        {
            tableModel.setDataVector(data, columns);
        }
    }

    public void refresh()
    {
        fetchDataAsync();
    }

    protected DefaultTableModel getTableModel()
    {
        return tableModel;
    }

    protected JTable getTable()
    {
        return table;
    }

    protected TableRowSorter<DefaultTableModel> getRowSorter()
    {
        return rowSorter;
    }

    protected abstract void fetchDataAsync();
}
