package graphdivider.view;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.MissingResourceException;

/**
 * Utility class for managing application language and localization.
 * Handles language switching, resource bundle loading, and notifies listeners on changes.
 */
public final class Language
{
    // Listeners for language change events (thread-safe)
    private static final List<Runnable> languageListeners = new CopyOnWriteArrayList<>();
    // Current application locale
    private static Locale currentLocale = Locale.getDefault();
    // Resource bundle for current locale
    private static ResourceBundle resourceBundle = loadResourceBundle(currentLocale);

    // Static block to initialize default locale and resource bundle
    static
    {
        try
        {
            Locale.setDefault(currentLocale);
            resourceBundle = loadResourceBundle(currentLocale);
        } 
        catch (MissingResourceException e)
        {
            System.err.println("Default resource bundle not found. Application cannot start.");
            throw new RuntimeException(e);
        }
    }

    // Prevent instantiation of utility class
    private Language() {}

    /**
     * Loads the resource bundle for the specified locale.
     * Falls back to English if the requested locale is not available.
     *
     * @param locale The locale to load.
     * @return The loaded ResourceBundle.
     */
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
        } 
        catch (MissingResourceException e)
        {
            // Fallback to English if the requested locale is not available
            return ResourceBundle.getBundle(
                "languages.Language",
                Locale.ENGLISH,
                ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT)
            );
        }
    }

    /**
     * Switches the application language to English and notifies listeners.
     */
    public static void applyEnglishLanguage()
    {
        currentLocale = Locale.ENGLISH;
        Locale.setDefault(currentLocale);
        resourceBundle = loadResourceBundle(currentLocale);
        notifyLanguageChangeListeners();
    }

    /**
     * Switches the application language to Polish and notifies listeners.
     */
    public static void applyPolishLanguage()
    {
        currentLocale = new Locale("pl", "PL");
        Locale.setDefault(currentLocale);
        resourceBundle = loadResourceBundle(currentLocale);
        notifyLanguageChangeListeners();
    }

    /**
     * Registers a listener to be notified when the language changes.
     *
     * @param listener Runnable to execute on language change.
     */
    public static void addLanguageChangeListener(Runnable listener)
    {
        languageListeners.add(listener);
    }

    /**
     * Notifies all registered language change listeners.
     */
    private static void notifyLanguageChangeListeners()
    {
        for (Runnable r : languageListeners)
        {
            r.run();
        }
    }

    /**
     * Gets the current application locale.
     *
     * @return The current Locale.
     */
    public static Locale getCurrentLocale()
    {
        return currentLocale;
    }

    /**
     * Gets a localized string for the given key from the resource bundle.
     *
     * @param key The key for the desired string.
     * @return The localized string.
     * @throws java.util.MissingResourceException if the key is not found.
     */
    public static String getString(String key)
    {
        return resourceBundle.getString(key);
    }
}