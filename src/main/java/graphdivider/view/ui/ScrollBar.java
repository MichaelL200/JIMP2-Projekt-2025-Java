package graphdivider.view.ui;

import graphdivider.view.Theme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

// Custom ScrollBar with theme support
public final class ScrollBar extends JScrollBar
{
    // Default scrollbar width
    private static final int DEFAULT_WIDTH = 17;

    public ScrollBar(int orientation)
    {
        super(orientation);
        setUI(new ModernScrollBarUI());
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_WIDTH));
        setOpaque(false);
        registerThemeListener();
    }

    // Listen for theme changes and update UI
    private void registerThemeListener() {
        Theme.addThemeChangeListener(() -> {
            setUI(new ModernScrollBarUI());
            repaint();
        });
    }

    // Custom ScrollBar UI (rounded, theme-aware)
    private static class ModernScrollBarUI extends BasicScrollBarUI
    {
        // Arc for rounded corners
        private static final int ARC = 16;

        // Thumb color based on theme
        private static Color getThumbColor()
        {
            return Theme.isDarkPreferred()
                    ? new Color(120, 144, 156, 220)
                    : new Color(120, 144, 156, 200);
        }

        // Track color based on theme
        private static Color getTrackColor()
        {
            return Theme.isDarkPreferred()
                    ? new Color(60, 63, 65, 180)
                    : new Color(230, 230, 230, 180);
        }

        // Paint thumb (the draggable part)
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
        {
            if (thumbBounds.isEmpty() || !c.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(getThumbColor());
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, ARC, ARC);
            } finally {
                g2.dispose();
            }
        }

        // Paint track (the background)
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(getTrackColor());
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, ARC, ARC);
            g2.dispose();
        }

        // Hide decrease button
        @Override
        protected JButton createDecreaseButton(int orientation)
        {
            return createZeroButton();
        }

        // Hide increase button
        @Override
        protected JButton createIncreaseButton(int orientation)
        {
            return createZeroButton();
        }

        // Create an invisible button (for arrows)
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