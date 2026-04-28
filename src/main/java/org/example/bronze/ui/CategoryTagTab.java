package org.example.bronze.ui;

import org.example.bronze.ui.util.Constants;
import org.example.bronze.ui.util.Refreshable;
import org.example.bronze.ui.util.ToastManager;
import org.example.bronze.util.DatabaseConfig;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CategoryTagTab extends JPanel implements Refreshable
{
    private static class CategoryItem
    {
        private Long id;
        private String name;

        public String toString()
        {
            return name;
        }
    }

    private static class TagItem
    {
        private Long id;
        private String name;

        public String toString()
        {
            return name;
        }
    }

    private JList<CategoryItem> categoryList;
    private DefaultListModel<CategoryItem> categoryModel;

    private JList<TagItem> tagList;
    private DefaultListModel<TagItem> tagModel;

    private JTextField newTagField;

    public CategoryTagTab()
    {
        setLayout(new BorderLayout());

        add(createMainPanel(), BorderLayout.CENTER);

        refresh();
    }

    private JPanel createMainPanel()
    {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        panel.add(createCategoryPanel());
        panel.add(createTagPanel());

        return panel;
    }

    private JPanel createCategoryPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());

        categoryModel = new DefaultListModel<>();
        categoryList = new JList<>(categoryModel);

        categoryList.addListSelectionListener(e ->
        {
            if (!e.getValueIsAdjusting())
            {
                loadTagsForCategory();
            }
        });

        panel.add(new JLabel("Categories"), BorderLayout.NORTH);
        panel.add(new JScrollPane(categoryList), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTagPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());

        tagModel = new DefaultListModel<>();
        tagList = new JList<>(tagModel);
        tagList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        newTagField = new JTextField();

        JButton addTagBtn = new JButton("Create Tag");
        JButton assignBtn = new JButton("Assign Selected");
        JButton removeBtn = new JButton("Remove Selected");

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(newTagField);
        top.add(addTagBtn);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.add(assignBtn);
        bottom.add(removeBtn);

        panel.add(new JLabel("Tags"), BorderLayout.NORTH);
        panel.add(new JScrollPane(tagList), BorderLayout.CENTER);
        panel.add(top, BorderLayout.SOUTH);
        panel.add(bottom, BorderLayout.EAST);

        addTagBtn.addActionListener(e -> createTagAsync());
        assignBtn.addActionListener(e -> assignTagsToCategoryAsync());
        removeBtn.addActionListener(e -> removeTagsFromCategoryAsync());

        return panel;
    }

    private void loadCategories()
    {
        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground()
            {
                categoryModel.clear();

                String sql = Constants.GET_ALL_CATEGORIES;

                try (Connection conn = DatabaseConfig.getDataSource().getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery())
                {
                    while (rs.next())
                    {
                        CategoryItem c = new CategoryItem();
                        c.id = rs.getLong(1);
                        c.name = rs.getString(2);

                        categoryModel.addElement(c);
                    }
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Unable to load categories", e);
                }
                return null;
            }
        };

        worker.execute();
    }

    private void loadTags()
    {
        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground()
            {
                tagModel.clear();

                String sql = Constants.GET_ALL_TAGS;

                try (Connection conn = DatabaseConfig.getDataSource().getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery())
                {
                    while (rs.next())
                    {
                        TagItem t = new TagItem();
                        t.id = rs.getLong(1);
                        t.name = rs.getString(2);

                        tagModel.addElement(t);
                    }
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Unable to load tags", e);

                    JOptionPane.showMessageDialog(CategoryTagTab.this, "Unable to load tags", e.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }
        };

        worker.execute();
    }

    private void createTagAsync()
    {
        String tagName = newTagField.getText().trim();
        newTagField.setText("");

        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground()
            {
                String sql = Constants.INSERT_TAG;

                try (Connection conn = DatabaseConfig.getDataSource().getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql))
                {
                    ps.setString(1, tagName);
                    ps.executeUpdate();
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Unable to create tag", e);
                    JOptionPane.showMessageDialog(CategoryTagTab.this, "Unable to create tag", e.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }

            @Override
            protected void done()
            {
                loadTags();
            }
        };

        worker.execute();
    }

    private void assignTagsToCategoryAsync()
    {
        CategoryItem category = categoryList.getSelectedValue();
        if (category == null) return;

        var selectedTags = tagList.getSelectedValuesList();

        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground()
            {
                String sql = Constants.ASSIGN_CATEGORY_TAG;

                try (Connection conn = DatabaseConfig.getDataSource().getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql))
                {
                    for (TagItem tag : selectedTags)
                    {
                        ps.setLong(1, category.id);
                        ps.setLong(2, tag.id);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Unable to assign tags", e);
                    JOptionPane.showMessageDialog(CategoryTagTab.this, "Unable to assign tags", e.getMessage(), JOptionPane.ERROR_MESSAGE);
                }

                return null;
            }

            @Override
            protected void done()
            {
                ToastManager.showToast(CategoryTagTab.this, "Tags assigned");
            }
        };

        worker.execute();
    }

    private void loadTagsForCategory()
    {
        CategoryItem category = categoryList.getSelectedValue();
        if (category == null) return;

        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground()
            {
                String sql = Constants.LOAD_CATEGORY_TAGS;

                try (Connection conn = DatabaseConfig.getDataSource().getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql))
                {
                    ps.setLong(1, category.id);

                    ResultSet rs = ps.executeQuery();

                    java.util.Set<Long> assigned = new java.util.HashSet<>();
                    while (rs.next())
                        assigned.add(rs.getLong(1));

                    // update UI selection on EDT
                    SwingUtilities.invokeLater(() ->
                    {
                        int[] indices = new int[tagModel.size()];
                        int idx = 0;

                        for (int i = 0; i < tagModel.size(); i++)
                        {
                            if (assigned.contains(tagModel.get(i).id))
                                indices[idx++] = i;
                        }

                        tagList.setSelectedIndices(java.util.Arrays.copyOf(indices, idx));
                    });
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Unable to load tags", e);
                    JOptionPane.showMessageDialog(CategoryTagTab.this, "Unable to load tags", e.getMessage(), JOptionPane.ERROR_MESSAGE);
                }

                return null;
            }
        };

        worker.execute();
    }

    private void removeTagsFromCategoryAsync()
    {
        CategoryItem category = categoryList.getSelectedValue();
        if (category == null) return;

        var selectedTags = tagList.getSelectedValuesList();
        if (selectedTags.isEmpty()) return;

        SwingWorker<Void, Void> worker = new SwingWorker<>()
        {
            @Override
            protected Void doInBackground()
            {
                String sql = Constants.DELETE_CATEGORY_TAG;

                try (java.sql.Connection conn = DatabaseConfig.getDataSource().getConnection();
                     java.sql.PreparedStatement ps = conn.prepareStatement(sql))
                {
                    for (TagItem tag : selectedTags)
                    {
                        ps.setLong(1, category.id);
                        ps.setLong(2, tag.id);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Unable to delete tags", e);
                    JOptionPane.showMessageDialog(CategoryTagTab.this, "Unable to delete tags", e.getMessage(), JOptionPane.ERROR_MESSAGE);
                }

                return null;
            }

            @Override
            protected void done()
            {
                try
                {
                    get();
                    ToastManager.showToast(CategoryTagTab.this, "Tags removed");
                    loadTagsForCategory(); // refresh selection state
                } catch (Exception e)
                {
                    org.example.bronze.util.Constants.logger.error("Unable to remove tags", e);

                    JOptionPane.showMessageDialog(
                            CategoryTagTab.this,
                            e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    @Override
    public void refresh()
    {
        loadCategories();
        loadTags();
    }
}
