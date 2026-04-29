package org.example.bronze.ui;

import org.example.bronze.ui.exceptions.DatabaseOperationException;
import org.example.bronze.ui.util.Constants;
import org.example.bronze.ui.util.Elements;
import org.example.bronze.ui.util.ToastManager;
import org.example.bronze.util.DatabaseConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class SchemasTab extends Tab
{
    public static final String[] COLUMNS = {
            "ID",
            "Applicable Type",
            "Validator Path",
            "Schema File Path"
    };

    private JComboBox<String> typeDropdown;

    public SchemasTab()
    {
        super(COLUMNS);

        setLayout(new BorderLayout());

        add(createTable(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.NORTH);

        loadSchemaTypes();
        refresh();
    }

    private JPanel createFormPanel()
    {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));

        JTextField idField = new JTextField();

        typeDropdown = new JComboBox<>();
        JTextField validatorPathField = new JTextField();

        JTextField filePathField = new JTextField();
        filePathField.setEditable(false);

        JButton browseBtn = new JButton("Browse");

        JTextField searchField = Elements.getSearchField(getRowSorter());

        panel.add(new JLabel("Search:"));
        panel.add(searchField);

        panel.add(new JLabel("Schema ID:"));
        panel.add(idField);

        panel.add(new JLabel("Applicable Type:"));
        panel.add(typeDropdown);

        panel.add(new JLabel("Validator Path (optional):"));
        panel.add(validatorPathField);

        panel.add(new JLabel("Schema File Path:"));
        panel.add(filePathField);

        panel.add(new JLabel(""));
        panel.add(browseBtn);

        JButton addBtn = new JButton("Add Schema");
        JButton editBtn = new JButton("Edit Schema");

        panel.add(addBtn);
        panel.add(editBtn);

        addBtn.addActionListener((ActionEvent e) ->
        {
            String id = idField.getText();
            String type = (String) typeDropdown.getSelectedItem();
            String validatorPath = validatorPathField.getText();
            String filePath = filePathField.getText();

            if (id == null || id.isBlank())
            {
                JOptionPane.showMessageDialog(this, "Schema ID is required");
                return;
            }

            if (type == null || type.isBlank())
            {
                JOptionPane.showMessageDialog(this, "Applicable type is required");
                return;
            }

            if (filePath == null || filePath.isBlank())
            {
                JOptionPane.showMessageDialog(this, "Schema file path is required");
                return;
            }

            idField.setText("");
            validatorPathField.setText("");
            filePathField.setText("");

            addSchemaAsync(id, type, validatorPath, filePath);
        });

        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int result = chooser.showOpenDialog(SchemasTab.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(
                        chooser.getSelectedFile().getAbsolutePath()
                );
            }
        });

        editBtn.addActionListener(e -> openEditDialog());

        return panel;
    }

    private void loadSchemaTypes()
    {
        try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(Constants.GET_SCHEMA_TYPES);
             java.sql.ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                typeDropdown.addItem(rs.getString(1));
            }
        } catch (SQLException e)
        {
            org.example.bronze.util.Constants.logger.error("Failed to load schema types", e);
        }
    }

    private void addSchemaAsync(String id, String type, String validatorPath, String filePath)
    {
        ToastManager.showToast(this, "Adding schema...");

        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                addSchemaToBackend(id, type, validatorPath, filePath);
                return null;
            }

            @Override
            protected void done()
            {
                try
                {
                    get();
                    ToastManager.showToast(SchemasTab.this, "Schema added successfully");
                    refresh();
                } catch (Exception e)
                {
                    JOptionPane.showMessageDialog(
                            SchemasTab.this,
                            e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    private void addSchemaToBackend(String id, String type, String validatorPath, String filePath)
            throws DatabaseOperationException
    {
        String sql = Constants.ADD_SCHEMA;

        try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, id);
            ps.setString(2, type);

            if (validatorPath == null || validatorPath.isBlank())
                ps.setNull(3, java.sql.Types.VARCHAR);
            else
                ps.setString(3, validatorPath);

            ps.setString(4, filePath);

            ps.executeUpdate();
        } catch (SQLException e)
        {
            org.example.bronze.util.Constants.logger.error("Failed to insert schema", e);
            throw new DatabaseOperationException("Failed to insert schema", e);
        }
    }

    @Override
    protected void fetchDataAsync()
    {
        SwingWorker<Object[][], Void> worker = new SwingWorker<>()
        {
            @Override
            protected Object[][] doInBackground() throws Exception
            {
                String sql = Constants.GET_ALL_SCHEMAS;
                java.util.List<Object[]> rows = new java.util.ArrayList<>();

                try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                     java.sql.ResultSet rs = ps.executeQuery())
                {
                    while (rs.next())
                    {
                        rows.add(new Object[]{
                                rs.getString("schema_id"),
                                rs.getString("schema_applicable_type"),
                                rs.getString("schema_custom_validator_path"),
                                rs.getString("schema_file_path")
                        });
                    }
                }

                return rows.toArray(new Object[0][]);
            }

            @Override
            protected void done()
            {
                try
                {
                    updateData(get());
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error(
                            "Failed to fetch schemas", e);

                    JOptionPane.showMessageDialog(
                            SchemasTab.this,
                            e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    private void openEditDialog()
    {
        int row = getTable().getSelectedRow();

        if (row == -1)
        {
            JOptionPane.showMessageDialog(this, "Select a schema first");
            return;
        }

        String id = getTableModel().getValueAt(row, 0).toString();
        String type = getTableModel().getValueAt(row, 1).toString();
        String validator = (String) getTableModel().getValueAt(row, 2);
        String filePath = getTableModel().getValueAt(row, 3).toString();

        showEditDialog(id, type, validator, filePath);
    }

    private void showEditDialog(String id, String type, String validator, String filePath)
    {
        JComboBox<String> typeBox = new JComboBox<>();
        loadSchemaTypesInto(typeBox);
        typeBox.setSelectedItem(type);

        JTextField validatorField = new JTextField(validator);
        JTextField fileField = new JTextField(filePath);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Applicable Type"));
        panel.add(typeBox);
        panel.add(new JLabel("Validator Path"));
        panel.add(validatorField);
        panel.add(new JLabel("Schema File Path"));
        panel.add(fileField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Schema",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION)
        {
            updateSchemaAsync(
                    id,
                    (String) typeBox.getSelectedItem(),
                    validatorField.getText(),
                    fileField.getText()
            );
        }
    }

    private void loadSchemaTypesInto(JComboBox<String> box)
    {
        try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(Constants.GET_SCHEMA_TYPES);
             java.sql.ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                box.addItem(rs.getString(1));
            }
        } catch (SQLException e)
        {
            org.example.bronze.util.Constants.logger.error("Failed to load schema types", e);
        }
    }

    private void updateSchemaAsync(String id, String type, String validator, String filePath)
    {
        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                String sql = Constants.UPDATE_SCHEMA;

                try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement(sql))
                {
                    ps.setString(1, type);

                    if (validator == null || validator.isBlank())
                        ps.setNull(2, java.sql.Types.VARCHAR);
                    else
                        ps.setString(2, validator);

                    ps.setString(3, filePath);
                    ps.setString(4, id);

                    ps.executeUpdate();
                }

                return null;
            }

            @Override
            protected void done()
            {
                try
                {
                    get();
                    JOptionPane.showMessageDialog(SchemasTab.this, "Schema updated");
                    refresh();
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Update failed", e);
                    JOptionPane.showMessageDialog(
                            SchemasTab.this,
                            e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }
}