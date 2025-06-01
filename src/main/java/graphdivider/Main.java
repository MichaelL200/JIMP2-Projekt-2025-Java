package graphdivider;

import graphdivider.view.Frame;
import graphdivider.view.ui.Theme;

import javax.swing.*;

// Entry point for the Graph Divider application.
public final class Main
{
    // Main method: starts the application.
    public static void main(String[] args)
    {
        // Launch on EDT
        SwingUtilities.invokeLater(new LaunchApplicationCommand());
    }

    // Command to launch the main window.
    private static class LaunchApplicationCommand implements Runnable
    {
        @Override
        public void run()
        {
            // Init theme (0=auto, 1=light, 2=dark)
            Theme.initTheme(0);

            // Create and show main window
            Frame frame = new Frame();
            frame.setVisible(true);
        }
    }
}

