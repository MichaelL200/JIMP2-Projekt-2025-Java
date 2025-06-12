package graphdivider;

import graphdivider.controller.GraphController;
import graphdivider.view.Frame;
import graphdivider.view.Theme;

import javax.swing.*;

/**
 * Entry point for the Graph Divider application.
 * <p>
 * Responsible for initializing the main window, setting up the MVC structure,
 * and applying the initial theme and language listeners.
 */
public final class Main
{
    // Prevent instantiation of utility class
    private Main()
    {
        // Utility class: do not instantiate
    }

    /**
     * Main method: starts the application.
     * <p>
     * Initializes the theme, creates the main window and controller,
     * and ensures the UI is launched on the Event Dispatch Thread.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args)
    {
        // Launch on EDT (Event Dispatch Thread) for Swing components
        SwingUtilities.invokeLater(() ->
        {
            // Initialize theme (0=auto, 1=light, 2=dark)
            Theme.initTheme(0);

            // Create main window and controller
            Frame frame = new Frame(); // Main application window
            GraphController controller = new GraphController(); // Controller for graph logic
            controller.setGraphView(frame.getGraphPanel());
            controller.registerViewListeners(frame);
            frame.setController(controller);

            // Set window size, close operation, and icon
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit app on close
            frame.setSize(1200, 800); // Default window size
            frame.setLocationRelativeTo(null); // Center on screen
            frame.updateWindowIcon(); // Set window icon according to theme

            // Listen for language changes and update all UI texts/tooltips
            graphdivider.view.Language.addLanguageChangeListener(() ->
            {
                frame.updateMenuLanguage();
            });

            frame.setVisible(true); // Show the main window
        });
    }
}