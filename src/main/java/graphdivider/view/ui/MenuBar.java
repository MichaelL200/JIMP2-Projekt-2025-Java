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
    private final JMenuItem loadTextGraphItem = new JMenuItem();
    private final JMenuItem loadPartitionedTextItem = new JMenuItem();
    private final JMenuItem loadPartitionedBinaryItem = new JMenuItem();
    private final JMenuItem savePartitionedTextItem = new JMenuItem();
    private final JMenuItem saveBinaryItem = new JMenuItem();

    // Theme radio buttons
    private final JRadioButtonMenuItem autoThemeItem = new JRadioButtonMenuItem();
    private final JRadioButtonMenuItem lightThemeItem = new JRadioButtonMenuItem();
    private final JRadioButtonMenuItem darkThemeItem = new JRadioButtonMenuItem();

    // Language radio buttons
    private final JRadioButtonMenuItem englishItem = new JRadioButtonMenuItem("English");
    private final JRadioButtonMenuItem polishItem = new JRadioButtonMenuItem("Polski");

    // Setup menus and items
    public MenuBar()
    {
        updateTexts();

        // Load menu
        loadMenu.add(loadTextGraphItem);
        loadMenu.addSeparator();
        loadMenu.add(loadPartitionedTextItem);
        loadMenu.add(loadPartitionedBinaryItem);
        add(loadMenu);

        // Save menu
        saveMenu.add(savePartitionedTextItem);
        saveMenu.add(saveBinaryItem);
        add(saveMenu);

        // Save options disabled by default
        savePartitionedTextItem.setEnabled(false);
        saveBinaryItem.setEnabled(false);

        // Theme menu
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(autoThemeItem);
        themeGroup.add(lightThemeItem);
        themeGroup.add(darkThemeItem);
        autoThemeItem.setSelected(true);
        themeMenu.add(autoThemeItem);
        themeMenu.add(lightThemeItem);
        themeMenu.add(darkThemeItem);
        add(themeMenu);

        // Language menu
        ButtonGroup languageGroup = new ButtonGroup();
        languageGroup.add(englishItem);
        languageGroup.add(polishItem);
        englishItem.setSelected(true); // Default language
        languageMenu.add(englishItem);
        languageMenu.add(polishItem);
        add(languageMenu);

        Language.addLanguageChangeListener(this::updateTexts);
    }

    // Setters for enabling/disabling items
    public void setSaveButtons(boolean enabled)
    {
        savePartitionedTextItem.setEnabled(enabled);
        saveBinaryItem.setEnabled(enabled);
    }

    // Update menu texts based on current language
    public void updateTexts()
    {
        // Update menu texts based on current language
        loadMenu.setText(Language.getString("menu.loadFile"));
        saveMenu.setText(Language.getString("menu.saveFile"));
        themeMenu.setText(Language.getString("menu.theme"));
        // Always show both languages in the menu title
        languageMenu.setText("Language / Język");

        // Update item texts
        loadTextGraphItem.setText(Language.getString("menu.graphText"));
        loadPartitionedTextItem.setText(Language.getString("menu.partitionedGraphText"));
        loadPartitionedBinaryItem.setText(Language.getString("menu.partitionedGraphBinary"));

        // Update save item texts
        savePartitionedTextItem.setText(Language.getString("menu.partitionedGraphText"));
        saveBinaryItem.setText(Language.getString("menu.partitionedGraphBinary"));

        // Update theme radio button texts
        autoThemeItem.setText(Language.getString("menu.systemTheme"));
        lightThemeItem.setText(Language.getString("menu.lightTheme"));
        darkThemeItem.setText(Language.getString("menu.darkTheme"));

        // Always show English/Polski for language radio buttons
        englishItem.setText("English");
        polishItem.setText("Polski");
    }

    // Only expose listeners for controller to attach logic
    public void addLoadTextGraphListener(ActionListener l) { loadTextGraphItem.addActionListener(l); }
    public void addLoadPartitionedTextListener(ActionListener l) { loadPartitionedTextItem.addActionListener(l); }
    public void addLoadPartitionedBinaryListener(ActionListener l) { loadPartitionedBinaryItem.addActionListener(l); }
    public void addSavePartitionedTextListener(ActionListener l) { savePartitionedTextItem.addActionListener(l); }
    public void addSaveBinaryListener(ActionListener l) { saveBinaryItem.addActionListener(l); }
    public void addAutoThemeListener(ActionListener l) { autoThemeItem.addActionListener(l); }
    public void addLightThemeListener(ActionListener l) { lightThemeItem.addActionListener(l); }
    public void addDarkThemeListener(ActionListener l) { darkThemeItem.addActionListener(l); }
    public void addEnglishLanguageListener(ActionListener l) { englishItem.addActionListener(l); }
    public void addPolishLanguageListener(ActionListener l) { polishItem.addActionListener(l); }
}