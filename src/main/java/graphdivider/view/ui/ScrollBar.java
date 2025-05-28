package graphdivider.view.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Custom ScrollBar with modern styling and theme support.
 */
public class ScrollBar extends JScrollBar
{
    private static final int DEFAULT_WIDTH = 17;

    /**
     * Constructs a ScrollBar with the given orientation and applies a modern UI.
     * Listens for theme changes to update its appearance dynamically.
     *
     * @param orientation JScrollBar.VERTICAL or JScrollBar.HORIZONTAL
     */
    public ScrollBar(int orientation)
    {
        super(orientation);
        setUI(new ModernScrollBarUI());
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_WIDTH));
        setOpaque(false);

        // Listen for theme changes and update UI accordingly
        Theme.addThemeChangeListener(() ->
        {
            setUI(new ModernScrollBarUI());
            repaint();
        });
    }

    /**
     * ModernScrollBarUI customizes the appearance of the scrollbar
     * to match the application's theme, with rounded corners and
     * color adaptation for dark and light modes.
     */
    private static class ModernScrollBarUI extends BasicScrollBarUI
    {
        // Corner arc for rounded rectangles
        private static final int ARC = 16;

        /**
         * Returns the thumb color based on the current theme.
         */
        private static Color getThumbColor()
        {
            return Theme.isDarkPreferred()
                    ? new Color(120, 144, 156, 220) // darker thumb for dark mode
                    : new Color(120, 144, 156, 200); // blue-grey, semi-transparent
        }

        /**
         * Returns the track color based on the current theme.
         */
        private static Color getTrackColor()
        {
            return Theme.isDarkPreferred()
                    ? new Color(60, 63, 65, 180) // dark track for dark mode
                    : new Color(230, 230, 230, 180); // light grey, semi-transparent
        }

        /**
         * Paints the scrollbar thumb with rounded corners and theme color.
         */
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(getThumbColor());
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, ARC, ARC);
            g2.dispose();
        }

        /**
         * Paints the scrollbar track with rounded corners and theme color.
         */
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(getTrackColor());
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, ARC, ARC);
            g2.dispose();
        }

        /**
         * Creates a zero-size button for the decrease arrow to hide it.
         */
        @Override
        protected JButton createDecreaseButton(int orientation)
        {
            return createZeroButton();
        }

        /**
         * Creates a zero-size button for the increase arrow to hide it.
         */
        @Override
        protected JButton createIncreaseButton(int orientation)
        {
            return createZeroButton();
        }

        /**
         * Utility method to create a zero-size, invisible button.
         */
        private JButton createZeroButton()
        {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            button.setFocusable(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setOpaque(false);
            return button;
        }
    }
}
