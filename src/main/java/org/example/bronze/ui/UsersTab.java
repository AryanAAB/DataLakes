package org.example.bronze.ui;

import org.example.bronze.ui.exceptions.DataAlreadyExistsException;
import org.example.bronze.ui.exceptions.DatabaseOperationException;
import org.example.bronze.ui.util.Constants;
import org.example.bronze.ui.util.Elements;
import org.example.bronze.ui.util.ToastManager;
import org.example.bronze.util.DatabaseConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class UsersTab extends Tab
{
    public static final String[] COLUMNS = {"ID", "Internal ID", "Name", "Is Active"};

    public UsersTab()
    {
        super(COLUMNS);

        setLayout(new BorderLayout());

        add(createTable(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.NORTH);

        refresh();
    }

    private JPanel createFormPanel()
    {
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JCheckBox isActiveBox = new JCheckBox();

        // SEARCH FIELD
        JTextField searchField = Elements.getSearchField(getRowSorter());

        formPanel.add(new JLabel("Search:"));
        formPanel.add(searchField);

        formPanel.add(new JLabel("Internal ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Is Active:"));
        formPanel.add(isActiveBox);

        JButton addButton = new JButton("Add User");
        JButton editBtn = new JButton("Edit User");

        formPanel.add(addButton);
        formPanel.add(editBtn);

        addButton.addActionListener((ActionEvent e) ->
        {
            String id = idField.getText();
            String name = nameField.getText();
            boolean isActive = isActiveBox.isSelected();

            idField.setText("");
            nameField.setText("");
            isActiveBox.setSelected(false);
            addUserAsync(id, name, isActive);
            refresh();
        });

        editBtn.addActionListener(e -> openEditDialog());

        return formPanel;
    }

    private void addUserAsync(String id, String name, boolean isActive)
    {
        ToastManager.showToast(this, "Adding user...");

        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                addUserToBackend(id, name, isActive);
                return null;
            }

            @Override
            protected void done()
            {
                try
                {
                    get();
                    ToastManager.showToast(UsersTab.this, "User added successfully");
                    refresh();
                } catch (Exception e)
                {
                    JOptionPane.showMessageDialog(UsersTab.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void addUserToBackend(String internalId, String name, boolean isActive)
            throws DataAlreadyExistsException, DatabaseOperationException
    {
        String sql = Constants.ADD_USER;

        try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, internalId);
            ps.setString(2, name);
            ps.setBoolean(3, isActive);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            String sqlState = e.getSQLState();

            // Duplicate user (UNIQUE constraint violation)
            if ("23505".equals(sqlState) || "23000".equals(sqlState))
            {
                org.example.bronze.util.Constants.logger.error("User with same ID already exists", e);
                throw new DataAlreadyExistsException(
                        "User with same ID already exists", e
                );
            }

            org.example.bronze.util.Constants.logger.error("Failed to add user to database", e);
            throw new DatabaseOperationException(
                    "Failed to add user to database", e
            );
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
                String sql = Constants.GET_ALL_USERS;
                java.util.List<Object[]> rows = new java.util.ArrayList<>();

                try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                     java.sql.ResultSet rs = ps.executeQuery())
                {
                    while (rs.next())
                    {
                        rows.add(new Object[]{
                                rs.getString("user_id"),
                                rs.getString("internal_id"),
                                rs.getString("name"),
                                rs.getBoolean("isactive")
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
                    Object[][] data = get();

                    updateData(data);

                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error(
                            "Failed to fetch users from database", e);

                    JOptionPane.showMessageDialog(
                            UsersTab.this,
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
            JOptionPane.showMessageDialog(this, "Select a user first");
            return;
        }

        Long userId = Long.parseLong(getTableModel().getValueAt(row, 0).toString());
        String internalId = getTableModel().getValueAt(row, 1).toString();
        String name = getTableModel().getValueAt(row, 2).toString();
        boolean isActive = (boolean) getTableModel().getValueAt(row, 3);

        showEditDialog(userId, internalId, name, isActive);
    }

    private void showEditDialog(Long userId, String internalId, String name, boolean isActive)
    {
        JTextField internalField = new JTextField(internalId);
        JTextField nameField = new JTextField(name);
        JCheckBox activeBox = new JCheckBox("Active", isActive);

        JPanel panel = new JPanel(new java.awt.GridLayout(0, 1));
        panel.add(new JLabel("Internal ID"));
        panel.add(internalField);
        panel.add(new JLabel("Name"));
        panel.add(nameField);
        panel.add(activeBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit User",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION)
        {
            updateUserAsync(
                    userId,
                    internalField.getText(),
                    nameField.getText(),
                    activeBox.isSelected()
            );
        }
    }

    private void updateUserAsync(Long userId, String internalId, String name, boolean isActive)
    {
        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                String sql = Constants.UPDATE_USER;

                try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement(sql))
                {
                    ps.setString(1, internalId);
                    ps.setString(2, name);
                    ps.setBoolean(3, isActive);
                    ps.setLong(4, userId);

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
                    JOptionPane.showMessageDialog(UsersTab.this, "User updated");

                    refresh();
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Unable to update user", e);
                    JOptionPane.showMessageDialog(UsersTab.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }
}
