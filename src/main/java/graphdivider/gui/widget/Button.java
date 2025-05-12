package graphdivider.gui.widget;

import javax.swing.*;
import javax.swing.border.EmptyBorder; // for paddling
import javax.swing.plaf.basic.BasicHTML; // for HTML content
import javax.swing.text.View; // for preferred size
import java.awt.*; // fonts, colors, layout
import java.awt.event.ComponentAdapter; // for dynamic font size
import java.awt.event.ComponentEvent;  //

/**
 * A custom JButton with application-wide styling.
 */
public class Button extends JButton
{
    /** Minimum and maximum font sizes for dynamic resizing */
    private static final int MIN_FONT = 20, MAX_FONT = 28;

    /** Default colors for button background and text */
    private static final Color PRIMARY_COLOR = new Color(30, 140, 255); // blue
    private static final Color HOVER_COLOR = new Color(24, 116, 205); // slightly darker blue on hover
    private static final Color TEXT_COLOR = Color.WHITE;

    public Button(String text)
    {
        super("<html><div style='text-align: center;'>" + text + "</div></html>");
        initStyle();

        /** Adjusts font size dynamically */
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                int width = getWidth();
                if (width <= 0) return;

                int fontSize = Math.max(MIN_FONT, width / 10);
                fontSize = Math.min(fontSize, MAX_FONT);
                setFont(new Font("Segoe UI", Font.BOLD, fontSize));
                setForeground(TEXT_COLOR);
                revalidate();
                repaint();
            }
        });
    }

    /** Applies the style. */
    private void initStyle()
    {
        applyBackgroundAndOpacity();
        applyBorderAndPadding();
        attachHoverEffect();
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

    /** Defines the preferred size based on the text length */
    @Override
    public Dimension getPreferredSize()
    {
        int maxWidth = 200; // Maksymalna szerokość przycisku
        String htmlText = "<html><div style='width: " + maxWidth + "px; text-align: center;'>" + getText() + "</div></html>";
        JLabel tempLabel = new JLabel(htmlText);
        tempLabel.setFont(getFont());
        tempLabel.setBorder(getBorder());
        Dimension size = tempLabel.getPreferredSize();
        Insets insets = getInsets();
        return new Dimension(size.width + insets.left + insets.right, size.height + insets.top + insets.bottom);
    }
}
