package graphdivider.view.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Custom menu bar for the application.
 * Provides file loading/saving and theme selection options.
 */
public final class MenuBar extends JMenuBar
{
    // Menu items for loading different types of graphs
    private final JMenuItem loadTextGraphItem = new JMenuItem("Graph (Text)…");
    private final JMenuItem loadPartitionedTextItem = new JMenuItem("Partitioned Graph (Text)…");
    private final JMenuItem loadPartitionedBinaryItem = new JMenuItem("Partitioned Graph (Binary)…");

    // Menu items for saving partitioned graphs
    private final JMenuItem savePartitionedTextItem = new JMenuItem("Partitioned Graph (Text)…");
    private final JMenuItem saveBinaryItem = new JMenuItem("Partitioned Graph (Binary)…");

    // Menu items for theme selection
    private final JMenuItem autoThemeItem = new JMenuItem("Auto");
    private final JMenuItem lightThemeItem = new JMenuItem("Light");
    private final JMenuItem darkThemeItem  = new JMenuItem("Dark");

    /**
     * Constructs the menu bar, sets up menus and menu items.
     */
    public MenuBar()
    {
        // Create and populate the "Load File" menu
        JMenu loadMenu = new JMenu("Load File");
        loadMenu.add(loadTextGraphItem);
        loadMenu.addSeparator();
        loadMenu.add(loadPartitionedTextItem);
        loadMenu.add(loadPartitionedBinaryItem);
        add(loadMenu);

        // Create and populate the "Save File" menu
        JMenu saveMenu = new JMenu("Save File");
        saveMenu.add(savePartitionedTextItem);
        saveMenu.add(saveBinaryItem);
        add(saveMenu);

        // Disable save options by default (enabled after loading a graph)
        savePartitionedTextItem.setEnabled(false);
        saveBinaryItem.setEnabled(false);

        // Create and populate the "Theme" menu
        JMenu themeMenu = new JMenu("Theme");
        themeMenu.add(autoThemeItem);
        themeMenu.add(lightThemeItem);
        themeMenu.add(darkThemeItem);
        add(themeMenu);
    }

    // --- Listener registration methods for menu actions ---

    /**
     * Registers a listener for loading a text graph.
     * @param l the ActionListener to add
     */
    public void addLoadTextGraphListener(ActionListener l)
    {
        loadTextGraphItem.addActionListener(l);
    }

    /**
     * Registers a listener for loading a partitioned text graph.
     * @param l the ActionListener to add
     */
    public void addLoadPartitionedTextListener(ActionListener l)
    {
        loadPartitionedTextItem.addActionListener(l);
    }

    /**
     * Registers a listener for loading a partitioned binary graph.
     * @param l the ActionListener to add
     */
    public void addLoadPartitionedBinaryListener(ActionListener l)
    {
        loadPartitionedBinaryItem.addActionListener(l);
    }

    /**
     * Registers a listener for saving a partitioned text graph.
     * @param l the ActionListener to add
     */
    public void addSavePartitionedTextListener(ActionListener l)
    {
        savePartitionedTextItem.addActionListener(l);
    }

    /**
     * Registers a listener for saving a partitioned binary graph.
     * @param l the ActionListener to add
     */
    public void addSaveBinaryListener(ActionListener l)
    {
        saveBinaryItem.addActionListener(l);
    }

    /**
     * Registers a listener for selecting the auto theme.
     * @param l the ActionListener to add
     */
    public void addAutoThemeListener(ActionListener l)
    {
        autoThemeItem.addActionListener(l);
    }

    /**
     * Registers a listener for selecting the light theme.
     * @param l the ActionListener to add
     */
    public void addLightThemeListener(ActionListener l)
    {
        lightThemeItem.addActionListener(l);
    }

    /**
     * Registers a listener for selecting the dark theme.
     * @param l the ActionListener to add
     */
    public void addDarkThemeListener(ActionListener l)
    {
        darkThemeItem.addActionListener(l);
    }
}
