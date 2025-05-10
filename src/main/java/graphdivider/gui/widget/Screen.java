package graphdivider.gui.widget;

import graphdivider.gui.navigation.Navigator;

import javax.swing.*;
import java.awt.*;

/**
 * Base class for all application screens.
 * <p>
 * Provides common layout, styling, and full-screen frame creation.
 * Subclasses should add their own components and event handlers.
 */
public abstract class Screen extends JPanel
{
    /** Reference to the Navigator for switching between screens. */
    protected final Navigator navigator;

    /**
     * Constructs a Screen with shared configuration.
     *
     * @param navigator the Navigator used to request screen changes
     */
    public Screen(Navigator navigator)
    {
        this.navigator = navigator;
        initScreen();  // apply common panel setup
    }

    /**
     * Initializes the panel with:
     * 1. A centered GridBagLayout for flexible component placement.
     * 2. A dark background for consistent theming.
     * 3. An empty border to provide padding around edges.
     */
    private void initScreen()
    {
        // 1) Center layout: places components in the middle by default
        setLayout(new GridBagLayout());

        // 2) Shared background color: dark gray for all screens
        setBackground(Color.DARK_GRAY);

        // 3) Padding: 50px on each side to avoid cramped content
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
    }

    /**
     * Wraps this panel in a full-screen JFrame.
     * <p>
     * Call frame.setVisible(true) to display.
     *
     * @return a JFrame ready to show this screen
     */
    public JFrame createFrame()
    {
        JFrame frame = new JFrame(); // create window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit app on close
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // maximize to full screen
        frame.setContentPane(this); // place this panel inside
        return frame;
    }
}
