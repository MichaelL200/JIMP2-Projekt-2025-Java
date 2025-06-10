package graphdivider.view.ui;

import graphdivider.view.Language;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Locale;

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

    // Language buttons
    private final JRadioButtonMenuItem englishItem = new JRadioButtonMenuItem("English");
    private final JRadioButtonMenuItem polishItem = new JRadioButtonMenuItem("Polski");

    // Menus (need to be fields for updateTexts)
    private final JMenu loadMenu = new JMenu("Load File");
    private final JMenu saveMenu = new JMenu("Save File");
    private final JMenu themeMenu = new JMenu("Theme");
    private final JMenu languageMenu = new JMenu("Language");

    // Setup menus and items
    public MenuBar()
    {
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
    }

    // Setters for enabling/disabling items
    public void setSaveButtons(boolean enabled)
    {
        savePartitionedTextItem.setEnabled(enabled);
        saveBinaryItem.setEnabled(enabled);
    }

    // Call this method to update all menu texts when language changes
    public void updateTexts()
    {
        Locale locale = Language.getCurrentLocale();
        boolean isPolish = locale != null && locale.getLanguage().equals("pl");

        // Menus
        loadMenu.setText(isPolish ? "Wczytaj plik" : "Load File");
        saveMenu.setText(isPolish ? "Zapisz plik" : "Save File");
        themeMenu.setText(isPolish ? "Motyw" : "Theme");
        languageMenu.setText(isPolish ? "Język" : "Language");

        // Load menu items
        loadTextGraphItem.setText(isPolish ? "Graf (tekst)..." : "Graph (Text)…");
        loadPartitionedTextItem.setText(isPolish ? "Podzielony graf (tekst)..." : "Partitioned Graph (Text)…");
        loadPartitionedBinaryItem.setText(isPolish ? "Podzielony graf (binarny)..." : "Partitioned Graph (Binary)…");

        // Save menu items
        savePartitionedTextItem.setText(isPolish ? "Podzielony graf (tekst)..." : "Partitioned Graph (Text)…");
        saveBinaryItem.setText(isPolish ? "Podzielony graf (binarny)..." : "Partitioned Graph (Binary)…");

        // Theme radio buttons
        autoThemeItem.setText(isPolish ? "Auto" : "Auto");
        lightThemeItem.setText(isPolish ? "Jasny" : "Light");
        darkThemeItem.setText(isPolish ? "Ciemny" : "Dark");

        // Language radio buttons
        englishItem.setText(isPolish ? "Angielski" : "English");
        polishItem.setText(isPolish ? "Polski" : "Polski");
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
    public void addEnglishLanguageListener(ActionListener l) {
        englishItem.addActionListener(l);
    }
    public void addPolishLanguageListener(ActionListener l) {
        polishItem.addActionListener(l);
    }
}