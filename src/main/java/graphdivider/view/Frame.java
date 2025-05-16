package graphdivider.view;

import graphdivider.view.ui.MenuBar;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * A custom JFrame that starts at 50% of screen size (and enforces it as minimum),
 * then immediately maximizes to full screen.
 */
public class Frame extends JFrame
{
    public Frame()
    {
        // Set the window title
        this.setTitle("Graph Divider");

        // Load and set the window icon
        setWindowIcon();

        // Obtain full screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Mkay, half width and height as initial and minimum size
        int halfWidth  = screenSize.width  / 2;
        int halfHeight = (int)(screenSize.height / 1.5);

        // Set the minimum size to 50% of screen
        this.setMinimumSize(new Dimension(halfWidth, halfHeight));
        // Set initial window size to 50% of screen
        this.setSize(halfWidth, halfHeight);

        // Ensure application exits when window is closed
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Maximize window to fill the screen
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Menu bar
        MenuBar menuBar = new MenuBar();
        setJMenuBar(menuBar);
        // File operations
        /*
        menuBar.addLoadTextGraphListener( e -> onLoadTextGraph() );
        menuBar.addLoadBinaryGraphListener( e -> onLoadBinaryGraph() );
        menuBar.addLoadPartitionedTextListener( e -> onLoadPartitionedText() );
        menuBar.addLoadPartitionedBinaryListener( e -> onLoadPartitionedBinary());
        menuBar.addSavePartitionedTextListener( e -> onSavePartitionedText() );
        menuBar.addSaveBinaryListener( e -> onSaveBinaryGraph() );
        */
    }

    /**
     * Loads the application icon from resources and sets it on this frame.
     */
    private void setWindowIcon()
    {
        try
        {
            // icon.png powinien być w src/main/resources
            Image icon = ImageIO.read(getClass().getResourceAsStream("/icon.png"));
            this.setIconImage(icon);
        }
        catch (IOException | IllegalArgumentException e)
        {
            // Jeśli nie uda się wczytać, wypisujemy tylko ostrzeżenie
            System.err.println("Warning: Unable to load window icon: " + e.getMessage());
        }
    }
}
