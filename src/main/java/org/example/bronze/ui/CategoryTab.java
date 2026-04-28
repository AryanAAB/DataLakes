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

public class CategoryTab extends Tab
{
    public static final String[] COLUMNS = {"ID", "Category Name"};

    public CategoryTab()
    {
        super(COLUMNS);

        setLayout(new BorderLayout());

        add(createTable(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.NORTH);

        refresh();
    }

    private JPanel createFormPanel()
    {
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField categoryNameField = new JTextField();

        // SEARCH FIELD
        JTextField searchField = Elements.getSearchField(getRowSorter());

        formPanel.add(new JLabel("Search:"));
        formPanel.add(searchField);

        formPanel.add(new JLabel("Category Name:"));
        formPanel.add(categoryNameField);

        JButton addButton = new JButton("Add Category");
        JButton editBtn = new JButton("Edit Category");

        formPanel.add(addButton);
        formPanel.add(editBtn);


        addButton.addActionListener((ActionEvent e) ->
        {
            String name = categoryNameField.getText();

            categoryNameField.setText("");
            addCategoryAsync(name);
            refresh();
        });

        editBtn.addActionListener(e -> openEditDialog());

        return formPanel;
    }

    private void addCategoryAsync(String name)
    {
        ToastManager.showToast(this, "Adding category...");

        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                addCategoryToBackend(name);
                return null;
            }

            @Override
            protected void done()
            {
                try
                {
                    get();
                    ToastManager.showToast(CategoryTab.this, "Category added successfully");
                    refresh();
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Unable to add category", e);
                    JOptionPane.showMessageDialog(CategoryTab.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void addCategoryToBackend(String name)
            throws DataAlreadyExistsException, DatabaseOperationException
    {
        String sql = Constants.ADD_CATEGORY;

        try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            String sqlState = e.getSQLState();

            // Duplicate Category (UNIQUE constraint violation)
            if ("23505".equals(sqlState) || "23000".equals(sqlState))
            {
                org.example.bronze.util.Constants.logger.error("Category with same name already exists", e);
                throw new DataAlreadyExistsException(
                        "Category with same name already exists", e
                );
            }

            org.example.bronze.util.Constants.logger.error("Failed to add category to database", e);
            throw new DatabaseOperationException(
                    "Failed to add category to database", e
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
                String sql = Constants.GET_ALL_CATEGORIES;
                java.util.List<Object[]> rows = new java.util.ArrayList<>();

                try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                     java.sql.ResultSet rs = ps.executeQuery())
                {
                    while (rs.next())
                    {
                        rows.add(new Object[]{
                                rs.getString("category_id"),
                                rs.getString("category_name")
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
                            "Failed to fetch categories from database", e);

                    JOptionPane.showMessageDialog(
                            CategoryTab.this,
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
            JOptionPane.showMessageDialog(this, "Select a category first");
            return;
        }

        Long categoryId = Long.parseLong(getTableModel().getValueAt(row, 0).toString());
        String categoryName = getTableModel().getValueAt(row, 1).toString();

        showEditDialog(categoryId, categoryName);
    }

    private void showEditDialog(Long categoryId, String categoryName)
    {
        JTextField categoryNameField = new JTextField(categoryName);

        JPanel panel = new JPanel(new java.awt.GridLayout(0, 1));
        panel.add(new JLabel("Category Name"));
        panel.add(categoryNameField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit Category",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION)
        {
            updateCategoryAsync(
                    categoryId,
                    categoryNameField.getText()
            );
        }
    }

    private void updateCategoryAsync(Long categoryId, String name)
    {
        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                String sql = Constants.UPDATE_CATEGORY;

                try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement(sql))
                {
                    ps.setString(1, name);
                    ps.setLong(2, categoryId);

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
                    JOptionPane.showMessageDialog(CategoryTab.this, "Category updated");

                    refresh();
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Unable to update category", e);
                    JOptionPane.showMessageDialog(CategoryTab.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }
}
