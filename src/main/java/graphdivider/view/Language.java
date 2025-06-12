package graphdivider.view;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.MissingResourceException;

// Menu for selecting application language
public final class Language
{
    private static final List<Runnable> languageListeners = new CopyOnWriteArrayList<>();
    private static Locale currentLocale = Locale.getDefault();
    private static ResourceBundle resourceBundle = loadResourceBundle(currentLocale);

    // Static block to initialize default locale and resource bundle
    static
    {
        try
        {
            Locale.setDefault(currentLocale);
            resourceBundle = loadResourceBundle(currentLocale);
        } catch (MissingResourceException e)
        {
            System.err.println("Default resource bundle not found. Application cannot start.");
            throw new RuntimeException(e);
        }
    }

    private Language() {}

    private static ResourceBundle loadResourceBundle(Locale locale)
    {
        try
        {
            return ResourceBundle.getBundle
            (
                "languages.Language",
                locale,
                ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT)
            );
        } catch (MissingResourceException e)
        {
            // Fallback to English if the requested locale is not available
            return ResourceBundle.getBundle(
                "languages.Language",
                Locale.ENGLISH,
                ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT)
            );
        }
    }

    // Change the application language
    public static void applyEnglishLanguage()
    {
        currentLocale = Locale.ENGLISH;
        Locale.setDefault(currentLocale);
        resourceBundle = loadResourceBundle(currentLocale);
        notifyLanguageChangeListeners();
    }
    public static void applyPolishLanguage()
    {
        currentLocale = new Locale("pl", "PL");
        Locale.setDefault(currentLocale);
        resourceBundle = loadResourceBundle(currentLocale);
        notifyLanguageChangeListeners();
    }

    // Change the application language
    public static void addLanguageChangeListener(Runnable listener)
    {
        languageListeners.add(listener);
    }

    // Change the application language to the specified locale
    private static void notifyLanguageChangeListeners()
    {
        for (Runnable r : languageListeners) r.run();
    }

    // Set the current locale and update the resource bundle
    public static Locale getCurrentLocale()
    {
        return currentLocale;
    }

    // Set the current locale and notify listeners
    public static String getString(String key)
    {
        return resourceBundle.getString(key);
    }
}