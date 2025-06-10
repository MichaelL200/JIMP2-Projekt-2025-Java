package graphdivider.view;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Locale;

// Menu for selecting application language
public final class Language
{
    private static final List<Runnable> languageListeners = new CopyOnWriteArrayList<>();
    private static Locale currentLocale = Locale.getDefault();

    private Language() {}

    // Apply English language to the application
    public static void applyEnglishLanguage()
    {
        setLocale(Locale.ENGLISH);
    }

    // Apply Polish language to the application
    public static void applyPolishLanguage()
    {
        setLocale(new Locale("pl", "PL"));
    }

    public static void addLanguageChangeListener(Runnable listener)
    {
        languageListeners.add(listener);
    }

    private static void notifyLanguageChangeListeners()
    {
        for (Runnable r : languageListeners) r.run();
    }

    public static void setLocale(Locale locale)
    {
        if (!locale.equals(currentLocale))
        {
            currentLocale = locale;
            Locale.setDefault(locale);
            notifyLanguageChangeListeners();
        }
    }

    public static Locale getCurrentLocale()
    {
        return currentLocale;
    }
}
