package graphdivider;

import graphdivider.controller.GraphController;
import graphdivider.view.Frame;
import graphdivider.view.ui.Theme;

import javax.swing.*;

// Main class serves as the entry point for the Graph Divider application
public final class Main
{
    // Private constructor to prevent instantiation of the Main class
    public static void main(String[] args)
    {
        // Ensure that the GUI is created and manipulated on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new LaunchApplicationCommand());
    }

    // Runnable implementation that encapsulates the logic to launch the application
    private static class LaunchApplicationCommand implements Runnable
    {
        // Initializes the application's theme and creates the main window
        @Override
        public void run()
        {
            // Initialize the application's theme.
            // The parameter determines the theme mode:
            // 0 = auto (system default), 1 = light mode, 2 = dark mode.
            Theme.initTheme(0);

            // Create the controller
            GraphController controller = new GraphController();

            // Create the main application window (Frame).
            // Pass the controller to the Frame
            Frame frame = new Frame(controller);

            // Make the main window visible to the user.
            frame.setVisible(true);
        }
    }
}
