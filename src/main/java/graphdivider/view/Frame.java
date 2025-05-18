package graphdivider.view;

import graphdivider.view.ui.MenuBar;
import graphdivider.view.ui.Theme;
import graphdivider.view.ui.ToolPanel;
import graphdivider.view.ui.Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * A custom JFrame that starts at 50% of screen size (and enforces it as minimum), then immediately maximizes to full screen and updates its icon with the theme.
 */
public class Frame extends JFrame
{
    public Frame()
    {
        // Set the window title
        this.setTitle("Graph Divider");

        // Initialize icon to match current theme
        boolean initialDark = Theme.isDarkPreferred();
        updateWindowIcon(initialDark);

        // Size logic
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int halfWidth  = screenSize.width  / 2;
        int halfHeight = (int)(screenSize.height / 1.5);
        this.setMinimumSize(new Dimension(halfWidth, halfHeight));
        this.setSize(halfWidth, halfHeight);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Menu bar
        MenuBar menuBar = new MenuBar();
        setJMenuBar(menuBar);

        // Create the tool panel
        ToolPanel toolPanel = new ToolPanel();

        // Set layout and add components
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolPanel, BorderLayout.WEST);

        // Create and add the Graph panel so its vertices are visible
        Graph graphPanel = new Graph();
        getContentPane().add(graphPanel, BorderLayout.CENTER);

        // Theme operations
        menuBar.addLightThemeListener(e ->
        {
            Theme.applyLightTheme();
        });
        menuBar.addDarkThemeListener(e ->
        {
            Theme.applyDarkTheme();
        });
        menuBar.addAutoThemeListener(e ->
        {
            Theme.applyAutoTheme(() ->
            {
                boolean dark = Theme.isDarkPreferred();
                updateWindowIcon(dark);
            });
        });

        // Register change listener to react when user updates settings
        toolPanel.addChangeListener(e ->
        {
            int parts  = toolPanel.getPartitions();
            int margin = toolPanel.getMargin();
            // TODO: pass these values to the controller
            System.out.println("Partitions: " + parts + ", Margin: " + margin + "%");
        });
    }

    /**
     * Sets the window icon depending on darkMode.
     *
     * @param darkMode true ⇒ use icon_dark.png; false ⇒ use icon_light.png
     */
    private void updateWindowIcon(boolean darkMode)
    {
        String resource = darkMode
            ? "/icon_dark.png"
            : "/icon_light.png";

        try
        {
            Image icon = ImageIO.read(getClass().getResourceAsStream(resource));
            setIconImage(icon);
        }
        catch (IOException | IllegalArgumentException e)
        {
            System.err.println("Warning: Unable to load window icon '"
                + resource + "': " + e.getMessage());
        }
    }
}
