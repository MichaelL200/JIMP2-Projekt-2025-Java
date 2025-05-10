package graphdivider.gui.widget;

import javax.swing.*;
import javax.swing.border.EmptyBorder; // for paddling
import java.awt.*; // fonts, colors, layout

/**
 * A custom JButton with application-wide styling.
 */
public class Button extends JButton
{
    // Define color constants
    private static final Color PRIMARY_COLOR = new Color(30, 140, 255); // blue
    private static final Color HOVER_COLOR = new Color(24, 116, 205); // slightly darker blue on hover
    private static final Color TEXT_COLOR = Color.WHITE;

    public Button(String text)
    {
        super(text);
        initStyle();
    }

    /** Applies the style. */
    private void initStyle()
    {
        applyFontAndTextColor();
        applyBackgroundAndOpacity();
        applyBorderAndPadding();
        attachHoverEffect();
    }

     /** Sets font and text color. */
    private void applyFontAndTextColor()
    {
        setFont(new Font("Segoe UI", Font.BOLD, 18));
        setForeground(TEXT_COLOR);
    }

    /** Configures background color and opacity settings. */
    private void applyBackgroundAndOpacity()
    {
        setBackground(PRIMARY_COLOR);
        setContentAreaFilled(true); // ensures background is painted
        setOpaque(true); // ensures the component is not transparent
    }

    /** Removes default borders and adds custom padding. */
    private void applyBorderAndPadding()
    {
        setBorderPainted(false); // no L&F border
        setFocusPainted(false); // no focus rectangle
        setBorder(new EmptyBorder(10, 20, 10, 20)); // padding around text
    }

    /** Adds a mouse listener to handle hover color changes. */
    private void attachHoverEffect()
    {
        addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e)
            {
                setBackground(HOVER_COLOR);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e)
            {
                setBackground(PRIMARY_COLOR);
            }
        });
    }
}
