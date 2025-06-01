package graphdivider.view.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

// Custom menu bar for the application.
// Provides file loading/saving and theme selection options.
public final class MenuBar extends JMenuBar
{
    // Menu items for loading different types of graphs
    private final JMenuItem loadTextGraphItem = new JMenuItem("Graph (Text)…"); // Load a plain text graph file
    private final JMenuItem loadPartitionedTextItem = new JMenuItem("Partitioned Graph (Text)…"); // Load a partitioned graph in text format
    private final JMenuItem loadPartitionedBinaryItem = new JMenuItem("Partitioned Graph (Binary)…"); // Load a partitioned graph in binary format

    // Menu items for saving partitioned graphs
    private final JMenuItem savePartitionedTextItem = new JMenuItem("Partitioned Graph (Text)…"); // Save partitioned graph as text
    private final JMenuItem saveBinaryItem = new JMenuItem("Partitioned Graph (Binary)…"); // Save partitioned graph as binary

    // Menu items for theme selection
    private final JMenuItem autoThemeItem = new JMenuItem("Auto");   // Use system theme
    private final JMenuItem lightThemeItem = new JMenuItem("Light"); // Force light theme
    private final JMenuItem darkThemeItem = new JMenuItem("Dark");   // Force dark theme

    // Constructs the menu bar, sets up menus and menu items.
    public MenuBar()
    {
        // Create and populate the "Load File" menu
        JMenu loadMenu = new JMenu("Load File");
        loadMenu.add(loadTextGraphItem);           // Option to load a plain text graph
        loadMenu.addSeparator();
        loadMenu.add(loadPartitionedTextItem);     // Option to load a partitioned text graph
        loadMenu.add(loadPartitionedBinaryItem);   // Option to load a partitioned binary graph
        add(loadMenu);

        // Create and populate the "Save File" menu
        JMenu saveMenu = new JMenu("Save File");
        saveMenu.add(savePartitionedTextItem);     // Option to save partitioned graph as text
        saveMenu.add(saveBinaryItem);              // Option to save partitioned graph as binary
        add(saveMenu);

        // Disable save options by default (enabled after loading a graph)
        savePartitionedTextItem.setEnabled(false);
        saveBinaryItem.setEnabled(false);

        // Create and populate the "Theme" menu
        JMenu themeMenu = new JMenu("Theme");
        themeMenu.add(autoThemeItem);              // Option to use system theme
        themeMenu.add(lightThemeItem);             // Option to force light theme
        themeMenu.add(darkThemeItem);              // Option to force dark theme
        add(themeMenu);
    }

    // LISTENER METHODS

    // Registers a listener for loading a text graph.
    public void addLoadTextGraphListener(ActionListener l)
    {
        loadTextGraphItem.addActionListener(l);
    }

    // Registers a listener for loading a partitioned text graph.
    public void addLoadPartitionedTextListener(ActionListener l)
    {
        loadPartitionedTextItem.addActionListener(l);
    }

    // Registers a listener for loading a partitioned binary graph.
    public void addLoadPartitionedBinaryListener(ActionListener l)
    {
        loadPartitionedBinaryItem.addActionListener(l);
    }

    // Registers a listener for saving a partitioned text graph.
    public void addSavePartitionedTextListener(ActionListener l)
    {
        savePartitionedTextItem.addActionListener(l);
    }

    // Registers a listener for saving a partitioned binary graph.
    public void addSaveBinaryListener(ActionListener l)
    {
        saveBinaryItem.addActionListener(l);
    }

    // Registers a listener for selecting the auto theme.
    public void addAutoThemeListener(ActionListener l)
    {
        autoThemeItem.addActionListener(l);
    }

    // Registers a listener for selecting the light theme.
    public void addLightThemeListener(ActionListener l)
    {
        lightThemeItem.addActionListener(l);
    }

    // Registers a listener for selecting the dark theme.
    public void addDarkThemeListener(ActionListener l)
    {
        darkThemeItem.addActionListener(l);
    }
}

