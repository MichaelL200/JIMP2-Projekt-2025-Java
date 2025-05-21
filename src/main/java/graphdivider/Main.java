package graphdivider;

import graphdivider.view.Frame;
import graphdivider.view.ui.Theme;

import javax.swing.*;

/**
 * Entry point for the Graph Divider application.
 * Initializes the look and feel, then launches the main window.
 * Uses the Command pattern to encapsulate the startup logic for thread safety and clarity.
 */
public final class Main
{
    /**
     * The main method is the starting point of the application.
     * It schedules the GUI initialization to run on the Event Dispatch Thread (EDT),
     * which is the standard practice for Swing applications to ensure thread safety.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args)
    {
        // Schedule the application launch on the Event Dispatch Thread (EDT) for thread safety.
        // This ensures that all Swing components are created and updated on the correct thread.
        SwingUtilities.invokeLater(new LaunchApplicationCommand());
    }

    /**
     * Command object that encapsulates the logic for launching the main application window.
     * Ensures that theme initialization and frame creation occur on the EDT.
     */
    private static class LaunchApplicationCommand implements Runnable
    {
        /**
         * This method is called when the Runnable is executed.
         * It initializes the application's theme and creates the main window.
         */
        @Override
        public void run()
        {
            // Initialize the application's theme.
            // The parameter determines the theme mode:
            // 0 = auto (system default), 1 = light mode, 2 = dark mode.
            Theme.initTheme(0);

            // Create the main application window (Frame).
            // The Frame class sets up the main user interface.
            Frame frame = new Frame();

            // Make the main window visible to the user.
            frame.setVisible(true);
        }
    }
}

