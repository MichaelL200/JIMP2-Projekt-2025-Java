package graphdivider.view.ui;

import graphdivider.view.Theme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Custom ScrollBar with theme support.
 * Uses a modern, rounded UI and adapts to light/dark themes.
 */
public final class ScrollBar extends JScrollBar
{
    // Default scrollbar width in pixels
    private static final int DEFAULT_WIDTH = 17;

    /**
     * Constructs a ScrollBar with the specified orientation.
     * Applies a custom UI and registers for theme changes.
     *
     * @param orientation JScrollBar orientation (VERTICAL or HORIZONTAL)
     */
    public ScrollBar(int orientation)
    {
        super(orientation);
        setUI(new ModernScrollBarUI());
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_WIDTH));
        setOpaque(false);
        registerThemeListener();
    }

    /**
     * Registers a listener to update the scrollbar UI when the theme changes.
     */
    private void registerThemeListener() 
    {
        Theme.addThemeListener(
            () ->
            {
                setUI(new ModernScrollBarUI());
                repaint();
            }
        );
    }

    /**
     * Custom ScrollBar UI with rounded corners and theme-aware colors.
     */
    private static class ModernScrollBarUI extends BasicScrollBarUI
    {
        // Arc radius for rounded corners
        private static final int ARC = 16;

        /**
         * Gets the thumb (draggable part) color based on the current theme.
         *
         * @return Color for the thumb.
         */
        private static Color getThumbColor()
        {
            return Theme.isDarkThemeActive()
                    ? new Color(120, 144, 156, 220)
                    : new Color(120, 144, 156, 200);
        }

        /**
         * Gets the track (background) color based on the current theme.
         *
         * @return Color for the track.
         */
        private static Color getTrackColor()
        {
            return Theme.isDarkThemeActive()
                    ? new Color(60, 63, 65, 180)
                    : new Color(230, 230, 230, 180);
        }

        /**
         * Paints the thumb (draggable part) of the scrollbar.
         *
         * @param g Graphics context.
         * @param c Component.
         * @param thumbBounds Bounds of the thumb.
         */
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
        {
            if (thumbBounds.isEmpty() || !c.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(getThumbColor());
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, ARC, ARC);
            g2.dispose();
        }

        /**
         * Paints the track (background) of the scrollbar.
         *
         * @param g Graphics context.
         * @param c Component.
         * @param trackBounds Bounds of the track.
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
         * Creates an invisible button for the decrease arrow.
         *
         * @param orientation Button orientation.
         * @return Invisible JButton.
         */
        @Override
        protected JButton createDecreaseButton(int orientation)
        {
            return createZeroButton();
        }

        /**
         * Creates an invisible button for the increase arrow.
         *
         * @param orientation Button orientation.
         * @return Invisible JButton.
         */
        @Override
        protected JButton createIncreaseButton(int orientation)
        {
            return createZeroButton();
        }

        /**
         * Creates an invisible button for scrollbar arrows.
         *
         * @return JButton with zero size and no border.
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