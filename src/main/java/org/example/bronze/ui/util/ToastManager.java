package org.example.bronze.ui.util;

import javax.swing.*;
import java.awt.*;

public class ToastManager
{
    public static void showToast(Component parent, String message)
    {
        JWindow window = new JWindow();

        JPanel panel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(40, 40, 40, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(label, BorderLayout.CENTER);

        window.add(panel);
        window.pack();

        // ----------- POSITIONING (bottom center of parent or screen) -----------
        Rectangle bounds;
        try
        {
            bounds = parent.getGraphicsConfiguration().getBounds();
            Point parentLoc = parent.getLocationOnScreen();
            bounds = new Rectangle(parentLoc.x, parentLoc.y, parent.getWidth(), parent.getHeight());
        } catch (Exception e)
        {
            bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        }

        int x = bounds.x + (bounds.width - window.getWidth()) / 2;
        int y = bounds.y + bounds.height - window.getHeight() - 50;

        window.setLocation(x, y);

        // ----------- FADE IN / OUT -----------
        window.setOpacity(0f);
        window.setAlwaysOnTop(true);
        window.setVisible(true);

        // Fade in
        new Timer(20, new AbstractAction()
        {
            float opacity = 0f;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e)
            {
                opacity += 0.05f;
                window.setOpacity(Math.min(opacity, 1f));
                if (opacity >= 1f)
                {
                    ((Timer) e.getSource()).stop();
                }
            }
        }).start();

        // Fade out after delay
        new Timer(2000, e ->
        {
            new Timer(20, new AbstractAction()
            {
                float opacity = 1f;

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    opacity -= 0.05f;
                    window.setOpacity(Math.max(opacity, 0f));
                    if (opacity <= 0f)
                    {
                        window.dispose();
                        ((Timer) e.getSource()).stop();
                    }
                }
            }).start();
        })
        {{
            setRepeats(false);
            start();
        }};
    }
}