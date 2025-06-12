package graphdivider.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * Theme utility for managing light, dark, and auto (system) modes.
 * Handles theme switching, system detection, and notifies listeners on changes.
 */
public final class Theme
{
    // Listeners for theme changes (thread-safe)
    private static final List<Runnable> themeChangeListeners = new CopyOnWriteArrayList<>();
    // Current theme mode (AUTO, LIGHT, DARK)
    private static ThemeMode currentThemeMode = ThemeMode.AUTO;

    // Prevent instantiation of utility class
    private Theme() {}

    /**
     * Adds a theme change listener.
     * The listener will be notified whenever the theme changes.
     * 
     * @param listener Runnable to execute on theme change.
     */
    public static void addThemeListener(Runnable listener)
    {
        themeChangeListeners.add(listener);
    }

    /**
     * Removes a previously added theme change listener.
     * 
     * @param listener Runnable to remove from notifications.
     */
    public static void removeThemeListener(Runnable listener)
    {
        themeChangeListeners.remove(listener);
    }

    /**
     * Notifies all registered theme change listeners.
     */
    private static void notifyThemeListeners()
    {
        for (Runnable r : themeChangeListeners)
        {
            r.run();
        }
    }

    /**
     * Sets the theme to auto mode (detects system preference).
     * Optionally runs a callback after theme is set.
     * 
     * @param onThemeChanged Callback to run after theme is set, or null.
     */
    public static void setAutoTheme(Runnable onThemeChanged)
    {
        currentThemeMode = ThemeMode.AUTO;
        if (isSystemDarkThemePreferred())
        {
            setDarkTheme();
        }
        else
        {
            setLightTheme();
        }
        if (onThemeChanged != null)
        {
            onThemeChanged.run();
        }
    }

    /**
     * Sets the theme to auto mode (detects system preference).
     * No callback.
     */
    public static void setAutoTheme()
    {
        setAutoTheme(null);
    }

    /**
     * Sets the theme to light mode.
     * Notifies listeners and refreshes all windows.
     */
    public static void setLightTheme()
    {
        currentThemeMode = ThemeMode.LIGHT;
        FlatLightLaf.setup();
        refreshAllSwingWindows();
        notifyThemeListeners();
    }

    /**
     * Sets the theme to dark mode.
     * Notifies listeners and refreshes all windows.
     */
    public static void setDarkTheme()
    {
        currentThemeMode = ThemeMode.DARK;
        FlatDarkLaf.setup();
        refreshAllSwingWindows();
        notifyThemeListeners();
    }

    /**
     * Initializes the theme system and sets up OS listeners for theme changes.
     * 
     * @param mode 0=auto, 1=light, 2=dark
     */
    public static void initTheme(int mode)
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        // Listen for Windows and macOS theme changes
        tk.addPropertyChangeListener("win.menu.dark", evt -> setAutoTheme());
        tk.addPropertyChangeListener("apple.awt.application.appearance", evt -> setAutoTheme());
        // Listen for GNOME theme changes
        Toolkit.getDefaultToolkit().addPropertyChangeListener("org.gnome.desktop.interface.color-scheme", evt -> setAutoTheme());
        // Listen for KDE config changes
        watchKdeConfig();

        switch (mode)
        {
            case 1 -> setLightTheme();
            case 2 -> setDarkTheme();
            default -> setAutoTheme();
        }
    }

    /**
     * Detects if the system prefers dark theme.
     * Supports Windows, macOS, GNOME, KDE, and WSL.
     * 
     * @return true if dark theme is preferred, false otherwise.
     */
    private static boolean isSystemDarkThemePreferred()
    {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWSL = os.contains("linux") && System.getenv("WSL_DISTRO_NAME") != null;
        if (os.contains("mac") || os.contains("darwin"))
        {
            return System.getProperty("apple.awt.application.appearance", "").contains("dark");
        }
        if (os.contains("win") || isWSL)
        {
            try
            {
                String regExe = isWSL ? "/mnt/c/Windows/System32/reg.exe" : "reg";
                Process process = Runtime.getRuntime().exec(new String[]
                {
                    regExe, "query",
                    "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v", "AppsUseLightTheme"
                });
                process.waitFor();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
                {
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        if (line.contains("AppsUseLightTheme"))
                        {
                            String[] parts = line.trim().split("\\s+");
                            int value = Integer.decode(parts[parts.length - 1]);
                            return value == 0;
                        }
                    }
                }
            }
            catch (Exception ignored)
            {
            }
            return "true".equalsIgnoreCase(System.getProperty("ide.win.menu.dark"));
        }
        return isGnomeDark() || isKdeDark();
    }

    /**
     * Checks if the current theme is dark.
     * 
     * @return true if dark theme is active, false otherwise.
     */
    public static boolean isDarkThemeActive()
    {
        if (currentThemeMode == ThemeMode.DARK)
        {
            return true;
        }
        if (currentThemeMode == ThemeMode.LIGHT)
        {
            return false;
        }
        return isSystemDarkThemePreferred();
    }

    /**
     * Refreshes all open Swing windows to apply the current theme.
     */
    private static void refreshAllSwingWindows()
    {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        for (Window w : Window.getWindows())
        {
            SwingUtilities.updateComponentTreeUI(w);
            w.invalidate();
            w.validate();
            w.repaint();
        }
    }

    /**
     * Checks if GNOME desktop is set to dark mode.
     * 
     * @return true if GNOME prefers dark, false otherwise.
     */
    private static boolean isGnomeDark()
    {
        try
        {
            Process p = new ProcessBuilder
            (
                "gsettings", "get",
                "org.gnome.desktop.interface",
                "color-scheme"
            ).start();
            p.waitFor();
            try (BufferedReader r = new BufferedReader
            (
                new InputStreamReader(p.getInputStream())
            ))
            {
                String line = r.readLine();
                return line != null && line.contains("prefer-dark");
            }
        }
        catch (IOException | InterruptedException e)
        {
            return false;
        }
    }

    /**
     * Checks if KDE desktop is set to dark mode in kdeglobals.
     * 
     * @return true if KDE prefers dark, false otherwise.
     */
    private static boolean isKdeDark()
    {
        Path cfg = Paths.get(System.getProperty("user.home"), ".config", "kdeglobals");
        if (Files.exists(cfg))
        {
            try (Stream<String> lines = Files.lines(cfg))
            {
                boolean inGeneral = false;
                for (String line : (Iterable<String>) lines::iterator)
                {
                    line = line.trim();
                    if (line.equals("[General]"))
                    {
                        inGeneral = true;
                    }
                    else if (inGeneral && line.startsWith("ColorScheme="))
                    {
                        return line.substring(line.indexOf('=')+1).toLowerCase().contains("dark");
                    }
                }
            }
            catch (IOException ignored) {}
        }
        return false;
    }

    /**
     * Watches KDE config for changes and auto-updates theme if needed.
     */
    private static void watchKdeConfig()
    {
        Path dir = Paths.get(System.getProperty("user.home"), ".config");
        try
        {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            Thread t = new Thread(
                () ->
                {
                    while (!Thread.currentThread().isInterrupted())
                    {
                        try
                        {
                            WatchKey key = watcher.take();
                            for (WatchEvent<?> ev : key.pollEvents())
                            {
                                if ("kdeglobals".equals(ev.context().toString()))
                                {
                                    setAutoTheme();
                                }
                            }
                            key.reset();
                        }
                        catch (InterruptedException ignored)
                        {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            );
            t.setDaemon(true);
            t.start();
        }
        catch (IOException ignored) {}
    }

    /**
     * Loads the window icon appropriate for the current theme.
     * 
     * @return Image object for the window icon, or null if loading fails.
     */
    public static Image loadSystemAwareWindowIcon()
    {
        String resource = isDarkThemeActive() ? "/icon/icon_dark.png" : "/icon/icon_light.png";
        try
        {
            java.io.InputStream iconStream = Theme.class.getResourceAsStream(resource);
            if (iconStream == null)
            {
                throw new IllegalArgumentException("Resource not found: " + resource);
            }
            return javax.imageio.ImageIO.read(iconStream);
        }
        catch (Exception e)
        {
            System.err.println("Warning: Unable to load window icon '" + resource + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Theme mode enum for tracking current theme state.
     */
    public enum ThemeMode 
    { 
        AUTO, LIGHT, DARK 
    }
}