package graphdivider.gui.widget;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Button extends JButton
{
    public Button(String text)
    {
        super(text);
        initStyle();
    }

    private void initStyle()
    {
        // Font and text size
        setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Background color
        setBackground(new Color(30, 140, 255)); // blue
        setForeground(Color.WHITE);
        // Round edges

        setBorder(new EmptyBorder(10, 20, 10, 20));

        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setBorderPainted(false);

        // Cursor effect
        addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                setBackground(new Color(24, 116, 205));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                setBackground(new Color(30, 144, 255));
            }
        });
    }
}
