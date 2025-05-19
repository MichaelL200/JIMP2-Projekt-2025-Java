package graphdivider;

import graphdivider.view.Frame;
import graphdivider.view.ui.Theme;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Graph Divider application.
 * Uses the Command pattern to encapsulate the launch logic.
 */
public class Main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new LaunchApplicationCommand());
    }

    /**
     * Command object for launching the application.
     */
    private static class LaunchApplicationCommand implements Runnable
    {
        @Override
        public void run()
        {
            Theme.initTheme(0);
            Frame frame = new Frame();
            frame.setVisible(true);
        }
    }
}
