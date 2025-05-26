package graphdivider.view.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ScrollBar extends JScrollBar
{
    private static final int DEFAULT_WIDTH = 17;

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

    private static class ModernScrollBarUI extends BasicScrollBarUI
    {
        private static final int ARC = 16;

        private static Color getThumbColor()
        {
            return Theme.isDarkPreferred()
                    ? new Color(120, 144, 156, 220) // darker thumb for dark mode
                    : new Color(120, 144, 156, 200); // blue-grey, semi-transparent
        }

        private static Color getTrackColor()
        {
            return Theme.isDarkPreferred()
                    ? new Color(60, 63, 65, 180) // dark track for dark mode
                    : new Color(230, 230, 230, 180); // light grey, semi-transparent
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(getThumbColor());
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, ARC, ARC);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(getTrackColor());
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, ARC, ARC);
            g2.dispose();
        }

        @Override
        protected JButton createDecreaseButton(int orientation)
        {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation)
        {
            return createZeroButton();
        }

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
