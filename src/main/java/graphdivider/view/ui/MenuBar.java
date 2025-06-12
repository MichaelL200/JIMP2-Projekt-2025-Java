package graphdivider.view.ui;

import graphdivider.view.Language;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Locale;

// Menu bar for file and theme actions
public final class MenuBar extends JMenuBar
{
    // Menu items and submenus
    private final JMenu loadMenu = new JMenu();
    private final JMenu saveMenu = new JMenu();
    private final JMenu themeMenu = new JMenu();
    private final JMenu languageMenu = new JMenu();

    // Menu items
    private final JMenuItem loadGraphTextMenuItem = new JMenuItem();
    private final JMenuItem loadPartitionedGraphTextMenuItem = new JMenuItem();
    private final JMenuItem loadPartitionedGraphBinaryMenuItem = new JMenuItem();
    private final JMenuItem savePartitionedGraphTextMenuItem = new JMenuItem();
    private final JMenuItem savePartitionedGraphBinaryMenuItem = new JMenuItem();

    // Theme radio buttons
    private final JRadioButtonMenuItem systemThemeMenuItem = new JRadioButtonMenuItem();
    private final JRadioButtonMenuItem lightThemeMenuItem = new JRadioButtonMenuItem();
    private final JRadioButtonMenuItem darkThemeMenuItem = new JRadioButtonMenuItem();

    // Language radio buttons
    private final JRadioButtonMenuItem englishLanguageMenuItem = new JRadioButtonMenuItem("English");
    private final JRadioButtonMenuItem polishLanguageMenuItem = new JRadioButtonMenuItem("Polski");

    // Setup menus and items
    public MenuBar()
    {
        updateMenuTexts();

        // Load menu
        loadMenu.add(loadGraphTextMenuItem);
        loadMenu.addSeparator();
        loadMenu.add(loadPartitionedGraphTextMenuItem);
        loadMenu.add(loadPartitionedGraphBinaryMenuItem);
        add(loadMenu);

        // Save menu
        saveMenu.add(savePartitionedGraphTextMenuItem);
        saveMenu.add(savePartitionedGraphBinaryMenuItem);
        add(saveMenu);

        // Save options disabled by default
        savePartitionedGraphTextMenuItem.setEnabled(false);
        savePartitionedGraphBinaryMenuItem.setEnabled(false);

        // Theme menu
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(systemThemeMenuItem);
        themeGroup.add(lightThemeMenuItem);
        themeGroup.add(darkThemeMenuItem);
        systemThemeMenuItem.setSelected(true);
        themeMenu.add(systemThemeMenuItem);
        themeMenu.add(lightThemeMenuItem);
        themeMenu.add(darkThemeMenuItem);
        add(themeMenu);

        // Language menu
        ButtonGroup languageGroup = new ButtonGroup();
        languageGroup.add(englishLanguageMenuItem);
        languageGroup.add(polishLanguageMenuItem);
        englishLanguageMenuItem.setSelected(true); // Default language
        languageMenu.add(englishLanguageMenuItem);
        languageMenu.add(polishLanguageMenuItem);
        add(languageMenu);

        Language.addLanguageChangeListener(() -> {
            updateMenuTexts();
            // Update tooltips for all open frames
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame instanceof graphdivider.view.Frame frame) {
                frame.getGraphPanel().updateTooltips();
            }
        });
    }

    // Setters for enabling/disabling items
    public void setSaveMenuItemsEnabled(boolean enabled)
    {
        savePartitionedGraphTextMenuItem.setEnabled(enabled);
        savePartitionedGraphBinaryMenuItem.setEnabled(enabled);
    }

    // Update menu texts based on current language
    public void updateMenuTexts()
    {
        loadMenu.setText(Language.getString("menu.loadFile"));
        saveMenu.setText(Language.getString("menu.saveFile"));
        themeMenu.setText(Language.getString("menu.theme"));
        languageMenu.setText("Language / Język");

        loadGraphTextMenuItem.setText(Language.getString("menu.graphText"));
        loadPartitionedGraphTextMenuItem.setText(Language.getString("menu.partitionedGraphText"));
        loadPartitionedGraphBinaryMenuItem.setText(Language.getString("menu.partitionedGraphBinary"));

        savePartitionedGraphTextMenuItem.setText(Language.getString("menu.partitionedGraphText"));
        savePartitionedGraphBinaryMenuItem.setText(Language.getString("menu.partitionedGraphBinary"));

        systemThemeMenuItem.setText(Language.getString("menu.systemTheme"));
        lightThemeMenuItem.setText(Language.getString("menu.lightTheme"));
        darkThemeMenuItem.setText(Language.getString("menu.darkTheme"));

        englishLanguageMenuItem.setText("English");
        polishLanguageMenuItem.setText("Polski");
    }

    // Only expose listeners for controller to attach logic
    public void addLoadGraphTextMenuItemListener(ActionListener l) { loadGraphTextMenuItem.addActionListener(l); }
    public void addLoadPartitionedGraphTextMenuItemListener(ActionListener l) { loadPartitionedGraphTextMenuItem.addActionListener(l); }
    public void addLoadPartitionedGraphBinaryMenuItemListener(ActionListener l) { loadPartitionedGraphBinaryMenuItem.addActionListener(l); }
    public void addSavePartitionedGraphTextMenuItemListener(ActionListener l) { savePartitionedGraphTextMenuItem.addActionListener(l); }
    public void addSavePartitionedGraphBinaryMenuItemListener(ActionListener l) { savePartitionedGraphBinaryMenuItem.addActionListener(l); }
    public void addSystemThemeMenuItemListener(ActionListener l) { systemThemeMenuItem.addActionListener(l); }
    public void addLightThemeMenuItemListener(ActionListener l) { lightThemeMenuItem.addActionListener(l); }
    public void addDarkThemeMenuItemListener(ActionListener l) { darkThemeMenuItem.addActionListener(l); }
    public void addEnglishLanguageMenuItemListener(ActionListener l) { englishLanguageMenuItem.addActionListener(l); }
    public void addPolishLanguageMenuItemListener(ActionListener l) { polishLanguageMenuItem.addActionListener(l); }
}