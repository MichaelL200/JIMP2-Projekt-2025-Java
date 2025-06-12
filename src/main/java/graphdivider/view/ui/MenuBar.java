package graphdivider.view.ui;

import graphdivider.view.Language;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Locale;

/*
 * Menu bar for file and theme actions in the Graph Divider application.
 * Provides access to loading/saving graphs, theme selection, and language switching.
 */
public final class MenuBar extends JMenuBar
{
    // Menus for different categories
    private final JMenu loadMenu = new JMenu();      // Load graph menu
    private final JMenu saveMenu = new JMenu();      // Save graph menu
    private final JMenu themeMenu = new JMenu();     // Theme selection menu
    private final JMenu languageMenu = new JMenu();  // Language selection menu

    // Menu items for loading and saving
    private final JMenuItem loadGraphTextMenuItem = new JMenuItem();             // Load text graph
    private final JMenuItem loadPartitionedGraphTextMenuItem = new JMenuItem();  // Load partitioned text graph
    private final JMenuItem loadPartitionedGraphBinaryMenuItem = new JMenuItem();// Load partitioned binary graph
    private final JMenuItem savePartitionedGraphTextMenuItem = new JMenuItem();  // Save partitioned text graph
    private final JMenuItem savePartitionedGraphBinaryMenuItem = new JMenuItem();// Save partitioned binary graph

    // Theme radio buttons
    private final JRadioButtonMenuItem systemThemeMenuItem = new JRadioButtonMenuItem(); // System theme
    private final JRadioButtonMenuItem lightThemeMenuItem = new JRadioButtonMenuItem();  // Light theme
    private final JRadioButtonMenuItem darkThemeMenuItem = new JRadioButtonMenuItem();   // Dark theme

    // Language radio buttons
    private final JRadioButtonMenuItem englishLanguageMenuItem = new JRadioButtonMenuItem("English"); // English
    private final JRadioButtonMenuItem polishLanguageMenuItem = new JRadioButtonMenuItem("Polski");   // Polish

    /**
     * Constructs the menu bar, initializes all menus and menu items,
     * and registers language change listeners for dynamic UI updates.
     */
    public MenuBar()
    {
        updateMenuTexts();

        // Load menu setup
        loadMenu.add(loadGraphTextMenuItem);
        loadMenu.addSeparator();
        loadMenu.add(loadPartitionedGraphTextMenuItem);
        loadMenu.add(loadPartitionedGraphBinaryMenuItem);
        add(loadMenu);

        // Save menu setup
        saveMenu.add(savePartitionedGraphTextMenuItem);
        saveMenu.add(savePartitionedGraphBinaryMenuItem);
        add(saveMenu);

        // Save options disabled by default
        savePartitionedGraphTextMenuItem.setEnabled(false);
        savePartitionedGraphBinaryMenuItem.setEnabled(false);

        // Theme menu setup
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(systemThemeMenuItem);
        themeGroup.add(lightThemeMenuItem);
        themeGroup.add(darkThemeMenuItem);
        systemThemeMenuItem.setSelected(true);
        themeMenu.add(systemThemeMenuItem);
        themeMenu.add(lightThemeMenuItem);
        themeMenu.add(darkThemeMenuItem);
        add(themeMenu);

        // Language menu setup
        ButtonGroup languageGroup = new ButtonGroup();
        languageGroup.add(englishLanguageMenuItem);
        languageGroup.add(polishLanguageMenuItem);
        englishLanguageMenuItem.setSelected(true); // Default language
        languageMenu.add(englishLanguageMenuItem);
        languageMenu.add(polishLanguageMenuItem);
        add(languageMenu);

        // Listen for language changes and update menu texts/tooltips
        Language.addLanguageChangeListener(() ->
        {
            updateMenuTexts();
            // Update tooltips for all open frames
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame instanceof graphdivider.view.Frame frame)
            {
                frame.getGraphPanel().updateTooltips();
            }
        });
    }

    /**
     * Enable or disable save menu items.
     * @param enabled true to enable, false to disable.
     */
    public void setSaveMenuItemsEnabled(boolean enabled)
    {
        savePartitionedGraphTextMenuItem.setEnabled(enabled);
        savePartitionedGraphBinaryMenuItem.setEnabled(enabled);
    }

    /**
     * Update menu texts and tooltips based on the current language.
     * Should be called after a language change.
     */
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

    // --- Listener registration methods for controller logic ---

    /**
     * Register a listener for loading a text graph.
     * @param l ActionListener to handle the event.
     */
    public void addLoadGraphTextMenuItemListener(ActionListener l)
    {
        loadGraphTextMenuItem.addActionListener(l);
    }

    /**
     * Register a listener for loading a partitioned text graph.
     * @param l ActionListener to handle the event.
     */
    public void addLoadPartitionedGraphTextMenuItemListener(ActionListener l)
    {
        loadPartitionedGraphTextMenuItem.addActionListener(l);
    }

    /**
     * Register a listener for loading a partitioned binary graph.
     * @param l ActionListener to handle the event.
     */
    public void addLoadPartitionedGraphBinaryMenuItemListener(ActionListener l)
    {
        loadPartitionedGraphBinaryMenuItem.addActionListener(l);
    }

    /**
     * Register a listener for saving a partitioned text graph.
     * @param l ActionListener to handle the event.
     */
    public void addSavePartitionedGraphTextMenuItemListener(ActionListener l)
    {
        savePartitionedGraphTextMenuItem.addActionListener(l);
    }

    /**
     * Register a listener for saving a partitioned binary graph.
     * @param l ActionListener to handle the event.
     */
    public void addSavePartitionedGraphBinaryMenuItemListener(ActionListener l)
    {
        savePartitionedGraphBinaryMenuItem.addActionListener(l);
    }

    /**
     * Register a listener for selecting the system theme.
     * @param l ActionListener to handle the event.
     */
    public void addSystemThemeMenuItemListener(ActionListener l)
    {
        systemThemeMenuItem.addActionListener(l);
    }

    /**
     * Register a listener for selecting the light theme.
     * @param l ActionListener to handle the event.
     */
    public void addLightThemeMenuItemListener(ActionListener l)
    {
        lightThemeMenuItem.addActionListener(l);
    }

    /**
     * Register a listener for selecting the dark theme.
     * @param l ActionListener to handle the event.
     */
    public void addDarkThemeMenuItemListener(ActionListener l)
    {
        darkThemeMenuItem.addActionListener(l);
    }

    /**
     * Register a listener for selecting the English language.
     * @param l ActionListener to handle the event.
     */
    public void addEnglishLanguageMenuItemListener(ActionListener l)
    {
        englishLanguageMenuItem.addActionListener(l);
    }

    /**
     * Register a listener for selecting the Polish language.
     * @param l ActionListener to handle the event.
     */
    public void addPolishLanguageMenuItemListener(ActionListener l)
    {
        polishLanguageMenuItem.addActionListener(l);
    }
}