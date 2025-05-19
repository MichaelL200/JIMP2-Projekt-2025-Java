package graphdivider.view.ui;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MenuBar extends JMenuBar
{
    private final JMenuItem loadTextGraphItem = new JMenuItem("Graph (Text)…");
    private final JMenuItem loadBinaryGraphItem = new JMenuItem("Graph (Binary)…");
    private final JMenuItem loadPartitionedTextItem = new JMenuItem("Partitioned Graph (Text)…");
    private final JMenuItem loadPartitionedBinaryItem = new JMenuItem("Partitioned Graph (Binary)…");
    private final JMenuItem savePartitionedTextItem = new JMenuItem("Partitioned Graph (Text)…");
    private final JMenuItem saveBinaryItem = new JMenuItem("Partitioned Graph (Binary)…");
    private final JMenuItem autoThemeItem = new JMenuItem("Auto");
    private final JMenuItem lightThemeItem = new JMenuItem("Light");
    private final JMenuItem darkThemeItem  = new JMenuItem("Dark");

    public MenuBar()
    {
        JMenu loadMenu = new JMenu("Load File");
        loadMenu.add(loadTextGraphItem);
        loadMenu.add(loadBinaryGraphItem);
        loadMenu.addSeparator();
        loadMenu.add(loadPartitionedTextItem);
        loadMenu.add(loadPartitionedBinaryItem);
        add(loadMenu);

        JMenu saveMenu = new JMenu("Save File");
        saveMenu.add(savePartitionedTextItem);
        saveMenu.add(saveBinaryItem);
        add(saveMenu);

        savePartitionedTextItem.setEnabled(false);
        saveBinaryItem.setEnabled(false);

        JMenu themeMenu = new JMenu("Theme");
        themeMenu.add(autoThemeItem);
        themeMenu.add(lightThemeItem);
        themeMenu.add(darkThemeItem);
        add(themeMenu);
    }

    public void addLoadTextGraphListener(ActionListener l)      { loadTextGraphItem.addActionListener(l); }
    public void addLoadBinaryGraphListener(ActionListener l)    { loadBinaryGraphItem.addActionListener(l); }
    public void addLoadPartitionedTextListener(ActionListener l){ loadPartitionedTextItem.addActionListener(l); }
    public void addLoadPartitionedBinaryListener(ActionListener l){ loadPartitionedBinaryItem.addActionListener(l); }
    public void addSavePartitionedTextListener(ActionListener l){ savePartitionedTextItem.addActionListener(l); }
    public void addSaveBinaryListener(ActionListener l)         { saveBinaryItem.addActionListener(l); }
    public void addAutoThemeListener(ActionListener l)          { autoThemeItem.addActionListener(l); }
    public void addLightThemeListener(ActionListener l)         { lightThemeItem.addActionListener(l); }
    public void addDarkThemeListener(ActionListener l)          { darkThemeItem.addActionListener(l); }
}
