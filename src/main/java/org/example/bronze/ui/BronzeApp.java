package org.example.bronze.ui;

import javax.swing.*;

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
