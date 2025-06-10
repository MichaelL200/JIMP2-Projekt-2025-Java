package graphdivider;

import graphdivider.controller.GraphController;
import graphdivider.view.Frame;
import graphdivider.view.Theme;

import javax.swing.*;

// Entry point for the Graph Divider application.
// Only responsible for wiring up MVC.
public final class Main
{
    // Main method: starts the application.
    public static void main(String[] args)
    {
        // Launch on EDT (Event Dispatch Thread) for Swing components
        SwingUtilities.invokeLater(() ->
        {
            // Init theme (0=auto, 1=light, 2=dark)
            Theme.initTheme(0);

            // Create main window and controller
            Frame frame = new Frame();
            GraphController controller = new GraphController();
            controller.setGraphView(frame.getGraphPanel());
            controller.registerViewListeners(frame);
            frame.setController(controller);

            // Set window size and default close operation
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800); // or another default size
            frame.setLocationRelativeTo(null); // center on screen
            frame.updateWindowIcon(); // set window icon according to theme

            // Listen for language changes and update menu
            graphdivider.view.Language.addLanguageChangeListener(() -> frame.updateMenuLanguage());

            frame.setVisible(true);
        });
    }
}