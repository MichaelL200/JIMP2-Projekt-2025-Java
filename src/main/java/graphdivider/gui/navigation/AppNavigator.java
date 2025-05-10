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
    * <ol>
    *   <li>Creates the main application window (JFrame) and sets its default size.</li>
    *   <li>Maximizes the window to occupy the full screen.</li>
    *   <li>Initializes a CardLayout for managing multiple screens.</li>
    *   <li>Sets the CardLayout container as the content pane of the frame.</li>
    * </ol>
    */
    public AppNavigator()
    {
        // 1) Create the main application window with a title
        frame = new JFrame("GraphDivider");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the default size to 50% of the screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width / 2;
        int height = screenSize.height / 2;
        frame.setSize(width, height);

        // Set the minimum size to prevent window from becoming too small
        frame.setMinimumSize(new Dimension(600, 400));

        // 2) Maximize the window to occupy the full screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // 3) Initialize CardLayout for screen navigation
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // 4) Set the container as the content pane of the frame
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
