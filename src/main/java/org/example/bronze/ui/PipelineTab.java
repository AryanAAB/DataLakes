package org.example.bronze.ui;

import org.example.bronze.ui.exceptions.DatabaseOperationException;
import org.example.bronze.ui.util.Constants;
import org.example.bronze.ui.util.Elements;
import org.example.bronze.ui.util.ToastManager;
import org.example.bronze.util.DatabaseConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PipelineTab extends Tab
{
    public static final String[] COLUMNS = {
            "ID", "Config Path", "Category", "User", "Is Active", "Created At"
    };

    private JComboBox<CategoryItem> categoryDropdown;
    private JComboBox<UserItem> userDropdown;

    public PipelineTab()
    {
        super(COLUMNS);

        setLayout(new BorderLayout());

        add(createTable(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.NORTH);

        refresh();
    }

    private JPanel createFormPanel()
    {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        JTextField configPathField = new JTextField();
        JButton browseBtn = new JButton("Browse");

        JCheckBox isActiveBox = new JCheckBox();

        categoryDropdown = new JComboBox<>();
        userDropdown = new JComboBox<>();

        categoryDropdown.setEditable(false);
        userDropdown.setEditable(false);

        refresh();

        browseBtn.addActionListener(e ->
        {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION)
            {
                configPathField.setText(
                        chooser.getSelectedFile().getAbsolutePath()
                );
            }
        });

        JTextField searchField = Elements.getSearchField(getRowSorter());

        formPanel.add(new JLabel("Search:"));
        formPanel.add(searchField);

        formPanel.add(new JLabel("Config Path:"));
        formPanel.add(configPathField);

        formPanel.add(new JLabel(""));
        formPanel.add(browseBtn);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryDropdown);

        formPanel.add(new JLabel("User:"));
        formPanel.add(userDropdown);

        formPanel.add(new JLabel("Is Active:"));
        formPanel.add(isActiveBox);

        JButton addBtn = new JButton("Add Pipeline");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addBtn);

        addBtn.addActionListener((ActionEvent e) ->
        {
            try
            {
                String path = configPathField.getText();
                boolean isActive = isActiveBox.isSelected();

                CategoryItem category = (CategoryItem) categoryDropdown.getSelectedItem();
                UserItem user = (UserItem) userDropdown.getSelectedItem();

                if (category == null || user == null || path.isEmpty())
                {
                    JOptionPane.showMessageDialog(this, "All fields are required");
                    return;
                }

                addPipelineAsync(path, isActive, category.id, user.id);

                configPathField.setText("");
                isActiveBox.setSelected(false);

            } catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void addPipelineAsync(String path, boolean isActive, Long categoryId, Long userId)
    {
        ToastManager.showToast(this, "Adding pipeline...");

        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                addPipelineToBackend(path, isActive, categoryId, userId);
                return null;
            }

            @Override
            protected void done()
            {
                try
                {
                    get();
                    ToastManager.showToast(PipelineTab.this, "Pipeline added");
                    refresh();
                } catch (Exception e)
                {
                    JOptionPane.showMessageDialog(PipelineTab.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void addPipelineToBackend(String path, boolean isActive, Long categoryId, Long userId)
            throws DatabaseOperationException
    {
        String sql = Constants.ADD_PIPELINE;

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, path);
            ps.setBoolean(2, isActive);
            ps.setLong(3, categoryId);
            ps.setLong(4, userId);

            ps.executeUpdate();
        } catch (SQLException e)
        {
            org.example.bronze.util.Constants.logger.error("Failed to add pipeline", e);
            throw new DatabaseOperationException("Failed to add pipeline", e);
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
                String sql = Constants.GET_ALL_PIPELINES;

                java.util.List<Object[]> rows = new java.util.ArrayList<>();

                try (Connection conn = DatabaseConfig.getDataSource().getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery())
                {
                    while (rs.next())
                    {
                        rows.add(new Object[]{
                                rs.getLong("pipelineId"),
                                rs.getString("configFilePath"),
                                rs.getString("category_name"),
                                rs.getString("internal_id"),
                                rs.getBoolean("isActive"),
                                rs.getTimestamp("createdAt")
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
                    org.example.bronze.util.Constants.logger.error("Failed to fetch pipelines", e);
                    JOptionPane.showMessageDialog(PipelineTab.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    @Override
    public void refresh()
    {
        super.refresh();
        loadCategories();
        loadUsers();
    }

    private void loadCategories()
    {
        DefaultComboBoxModel<CategoryItem> categoryModel = new DefaultComboBoxModel<>();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.GET_ALL_CATEGORIES);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                categoryModel.addElement(new CategoryItem(
                        rs.getLong("category_id"),
                        rs.getString("category_name")
                ));
            }
        } catch (SQLException e)
        {
            org.example.bronze.util.Constants.logger.error("Failed to load categories", e);
        }

        categoryDropdown.setModel(categoryModel);
    }

    private void loadUsers()
    {
        DefaultComboBoxModel<UserItem> userModel = new DefaultComboBoxModel<>();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.GET_ALL_USERS);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                userModel.addElement(new UserItem(
                        rs.getLong("user_id"),
                        rs.getString("internal_id")
                ));
            }
        } catch (SQLException e)
        {
            org.example.bronze.util.Constants.logger.error("Failed to load users", e);
        }

        userDropdown.setModel(userModel);
    }

    private static class CategoryItem
    {
        Long id;
        String name;

        public CategoryItem(Long id, String name)
        {
            this.id = id;
            this.name = name;
        }

        public String toString()
        {
            return name;
        }
    }

    private static class UserItem
    {
        Long id;
        String internalId;

        public UserItem(Long id, String internalId)
        {
            this.id = id;
            this.internalId = internalId;
        }

        public String toString()
        {
            return internalId;
        }
    }
}