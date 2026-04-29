package org.example.bronze.ui;

import org.example.bronze.ui.util.Refreshable;

import javax.swing.*;
import java.awt.*;

public class BronzeApp extends JFrame
{
    public BronzeApp()
    {
        setTitle("Bronze Admin App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs as separate classes
        tabbedPane.add("Users", new UsersTab());
        tabbedPane.add("Pipeline Categories", new CategoryTab());
        tabbedPane.add("Assign Tags to Categories", new CategoryTagTab());
        tabbedPane.add("Pipelines", new PipelineTab());
        tabbedPane.addChangeListener(e ->
        {
            Component selected = tabbedPane.getSelectedComponent();

            if (selected instanceof Refreshable refreshable)
            {
                refreshable.refresh();
            }
        });

        add(tabbedPane);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            BronzeApp app = new BronzeApp();
            app.setVisible(true);
        });
    }
}
