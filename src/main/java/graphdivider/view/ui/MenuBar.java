package graphdivider.view.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * MenuBar with separate Load File and Save File menus, plus Theme switch.
 */
public class MenuBar extends JMenuBar
{
    // Load File items
    private final JMenuItem loadTextGraphItem;
    private final JMenuItem loadBinaryGraphItem;
    private final JMenuItem loadPartitionedTextItem;
    private final JMenuItem loadPartitionedBinaryItem;

    // Save File items
    private final JMenuItem savePartitionedTextItem;
    private final JMenuItem saveBinaryItem;

    // Theme items
    private final JMenuItem autoThemeItem;
    private final JMenuItem lightThemeItem;
    private final JMenuItem darkThemeItem;

    public MenuBar()
    {
        // --- Load File menu
        JMenu loadMenu = new JMenu("Load File");
        loadTextGraphItem = new JMenuItem("Graph (Text)…");
        loadBinaryGraphItem = new JMenuItem("Graph (Binary)…");
        loadPartitionedTextItem = new JMenuItem("Partitioned Graph (Text)…");
        loadPartitionedBinaryItem = new JMenuItem("Partitioned Graph (Binary)…");
        loadMenu.add(loadTextGraphItem);
        loadMenu.add(loadBinaryGraphItem);
        loadMenu.addSeparator();
        loadMenu.add(loadPartitionedTextItem);
        loadMenu.add(loadPartitionedBinaryItem);
        this.add(loadMenu);

        // --- Save File menu
        JMenu saveMenu = new JMenu("Save File");
        savePartitionedTextItem = new JMenuItem("Partitioned Graph (Text)…");
        saveBinaryItem = new JMenuItem("Partitioned Graph (Binary)…");
        saveMenu.add(savePartitionedTextItem);
        saveMenu.add(saveBinaryItem);
        this.add(saveMenu);

        // --- Theme menu
        JMenu themeMenu = new JMenu("Theme");
        autoThemeItem = new JMenuItem("Auto");
        lightThemeItem = new JMenuItem("Light");
        darkThemeItem  = new JMenuItem("Dark");
        themeMenu.add(autoThemeItem);
        themeMenu.add(lightThemeItem);
        themeMenu.add(darkThemeItem);
        this.add(themeMenu);
    }

    // Load File listeners
    public void addLoadTextGraphListener(ActionListener l)
    {
        loadTextGraphItem.addActionListener(l);
    }
    public void addLoadBinaryGraphListener(ActionListener l)
    {
        loadBinaryGraphItem.addActionListener(l);
    }
    public void addLoadPartitionedTextListener(ActionListener l)
    {
        loadPartitionedTextItem.addActionListener(l);
    }
    public void addLoadPartitionedBinaryListener(ActionListener l)
    {
        loadPartitionedBinaryItem.addActionListener(l);
    }

    // Save File listeners
    public void addSavePartitionedTextListener(ActionListener l)
    {
        savePartitionedTextItem.addActionListener(l);
    }
    public void addSaveBinaryListener(ActionListener l)
    {
        saveBinaryItem.addActionListener(l);
    }

    // Theme listeners
    public void addAutoThemeListener(ActionListener l)
    {
        autoThemeItem.addActionListener(l);
    }
    public void addLightThemeListener(ActionListener l)
    {
        lightThemeItem.addActionListener(l);
    }
    public void addDarkThemeListener(ActionListener l)
    {
        darkThemeItem.addActionListener(l);
    }
}
