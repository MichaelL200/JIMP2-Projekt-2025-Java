package graphdivider.view.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

// Menu bar for file and theme actions
public final class MenuBar extends JMenuBar
{
    // File load menu items
    private final JMenuItem loadTextGraphItem = new JMenuItem("Graph (Text)…");
    private final JMenuItem loadPartitionedTextItem = new JMenuItem("Partitioned Graph (Text)…");
    private final JMenuItem loadPartitionedBinaryItem = new JMenuItem("Partitioned Graph (Binary)…");

    // File save menu items
    private final JMenuItem savePartitionedTextItem = new JMenuItem("Partitioned Graph (Text)…");
    private final JMenuItem saveBinaryItem = new JMenuItem("Partitioned Graph (Binary)…");

    // Theme radio buttons
    private final JRadioButtonMenuItem autoThemeItem = new JRadioButtonMenuItem("Auto");
    private final JRadioButtonMenuItem lightThemeItem = new JRadioButtonMenuItem("Light");
    private final JRadioButtonMenuItem darkThemeItem = new JRadioButtonMenuItem("Dark");

    // Setup menus and items
    public MenuBar()
    {
        // Load menu
        JMenu loadMenu = new JMenu("Load File");
        loadMenu.add(loadTextGraphItem);
        loadMenu.addSeparator();
        loadMenu.add(loadPartitionedTextItem);
        loadMenu.add(loadPartitionedBinaryItem);
        add(loadMenu);

        // Save menu
        JMenu saveMenu = new JMenu("Save File");
        saveMenu.add(savePartitionedTextItem);
        saveMenu.add(saveBinaryItem);
        add(saveMenu);

        // Save options disabled by default
        savePartitionedTextItem.setEnabled(false);
        saveBinaryItem.setEnabled(false);

        // Theme menu
        JMenu themeMenu = new JMenu("Theme");
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(autoThemeItem);
        themeGroup.add(lightThemeItem);
        themeGroup.add(darkThemeItem);
        autoThemeItem.setSelected(true);

        themeMenu.add(autoThemeItem);
        themeMenu.add(lightThemeItem);
        themeMenu.add(darkThemeItem);
        add(themeMenu);
    }

    // --- Listener registration ---
    public void addLoadTextGraphListener(ActionListener l) {
        loadTextGraphItem.addActionListener(l);
    }
    public void addLoadPartitionedTextListener(ActionListener l) {
        loadPartitionedTextItem.addActionListener(l);
    }
    public void addLoadPartitionedBinaryListener(ActionListener l) {
        loadPartitionedBinaryItem.addActionListener(l);
    }
    public void addSavePartitionedTextListener(ActionListener l) {
        savePartitionedTextItem.addActionListener(l);
    }
    public void addSaveBinaryListener(ActionListener l) {
        saveBinaryItem.addActionListener(l);
    }
    public void addAutoThemeListener(ActionListener l) {
        autoThemeItem.addActionListener(l);
    }
    public void addLightThemeListener(ActionListener l) {
        lightThemeItem.addActionListener(l);
    }
    public void addDarkThemeListener(ActionListener l) {
        darkThemeItem.addActionListener(l);
    }
}

