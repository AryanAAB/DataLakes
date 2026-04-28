package org.example.bronze.ui.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public final class Elements
{
    public static JTextField getSearchField(TableRowSorter<DefaultTableModel> sorter)
    {
        JTextField searchField = new JTextField();

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener()
        {
            private void filter()
            {
                String text = searchField.getText().trim();

                if (text.isEmpty())
                {
                    sorter.setRowFilter(null);
                }
                else
                {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e)
            {
                filter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e)
            {
                filter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e)
            {
                filter();
            }
        });

        return searchField;
    }
}
