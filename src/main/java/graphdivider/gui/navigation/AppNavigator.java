package graphdivider.gui.navigation;

import javax.swing.*;
import java.awt.*;

/**
 * Implementation of Navigator using CardLayout to switch between different screens.
 * <p>
 * This class creates a single JFrame that contains a JPanel with CardLayout. Each registered screen is added to the container with a unique ID, and calling show(id) will display the corresponding screen.
 */
public class AppNavigator implements Navigator
{
    /** Main application window. */
    private final JFrame frame;
    /** Layout manager that treats each screen as a "card" and shows one at a time. */
    private final CardLayout cardLayout;
    /** Container panel holding all registered screens under CardLayout. */
    private final JPanel container;

    /**
     * Constructs the AppNavigator:
     * 1. Creates the main JFrame and configures it for full-screen display.
     * 2. Initializes a CardLayout and wraps it in a JPanel.
     * 3. Sets the container as the content pane of the frame.
     */
    public AppNavigator()
    {
        // 1) Create and configure the application window
        frame = new JFrame("GraphDivider");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // 2) Set up CardLayout for screen navigation
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // 3) Place the container in the frame
        frame.setContentPane(container);
    }

    /**
     * Registers a new screen with the navigator.
     *
     * @param screenId unique string identifier for the screen
     * @param screen   the JPanel representing the screen's UI
     */
    public void register(String screenId, JPanel screen)
    {
        container.add(screen, screenId);
    }

    /**
     * Displays the screen associated with the given ID.
     * If the frame is not yet visible, makes it visible on the Event Dispatch Thread.
     *
     * @param screenId the ID of the screen to show
     */
    @Override
    public void show(String screenId)
    {
        // Switch to the desired card
        cardLayout.show(container, screenId);

        // If this is the first time, show the window
        if (!frame.isVisible())
        {
            SwingUtilities.invokeLater(() -> frame.setVisible(true));
        }
    }

    /**
     * Provides access to the main JFrame for additional configuration, such as adding menus, toolbars, or custom window icons.
     *
     * @return the primary application JFrame
     */
    public JFrame getFrame()
    {
        return frame;
    }
}
