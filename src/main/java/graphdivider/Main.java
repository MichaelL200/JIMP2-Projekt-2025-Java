package graphdivider;

import graphdivider.controller.GraphController;
import graphdivider.view.Frame;
import graphdivider.view.ui.Theme;

import javax.swing.*;

// Entry point for the Graph Divider application.
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

            frame.setVisible(true);
        });
    }
}

