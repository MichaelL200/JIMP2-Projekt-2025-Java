package graphdivider;

import graphdivider.view.Frame;
import graphdivider.view.ui.Theme;

import javax.swing.SwingUtilities;

public class Main
{
    public static void main(String[] args)
    {
        // Swing components must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                // Instantiate and display our custom Frame
                Frame frame = new Frame();
                frame.setVisible(true);
                Theme.initTheme(0); // 0 = Auto, or 1 = Light, 2 = Dark
            }
        });
    }
}
