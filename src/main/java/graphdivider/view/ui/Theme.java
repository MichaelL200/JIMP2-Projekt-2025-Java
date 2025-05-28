package graphdivider.view.ui;

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
 * Theme management utility for the application.
 * Handles light, dark, and auto themes based on system preferences.
 * Provides methods to register listeners for theme changes and apply themes.
 * Supports dynamic updates of UI components when the theme changes.
 */
public final class Theme
{
    // Thread-safe list of listeners to notify when the theme changes
    private static final List<Runnable> themeListeners = new CopyOnWriteArrayList<>();
    // Tracks the current theme mode (auto, light, dark)
    private static ThemeMode currentTheme = ThemeMode.AUTO;

    /**
     * Enum representing the available theme modes.
     */
    public enum ThemeMode { AUTO, LIGHT, DARK }

    // Private constructor to prevent instantiation of utility class
    private Theme() {}

    /**
     * Registers a listener to be notified when the theme changes.
     *
     * @param listener Runnable to execute on theme change
     */
    public static void addThemeChangeListener(Runnable listener)
    {
        themeListeners.add(listener);
    }

    /**
     * Removes a previously registered theme change listener.
     * @param listener Runnable to remove
     */
    public static void removeThemeChangeListener(Runnable listener)
    {
        themeListeners.remove(listener);
    }

    /**
     * Notifies all registered listeners that the theme has changed.
     */
    private static void notifyThemeChangeListeners()
    {
        for (Runnable r : themeListeners) r.run();
    }

    /**
     * Updates the color of all edges in the graph to match the current theme.
     * This is called after a theme change to ensure visual consistency.
     */
    public static void updateAllEdgesColor()
    {
        graphdivider.view.ui.graph.Edge.updateAllEdgesColor();
    }

    /**
     * Applies the specified theme mode (AUTO, LIGHT, DARK).
     * If AUTO, detects the system's preferred theme.
     * Notifies listeners after applying.
     * @param mode ThemeMode to apply
     */
    public static void applyTheme(ThemeMode mode)
    {
        currentTheme = mode;
        if (mode == ThemeMode.AUTO)
        {
            if (isSystemDark())
                setupTheme(ThemeMode.DARK);
            else
                setupTheme(ThemeMode.LIGHT);
        }
        else
        {
            setupTheme(mode);
        }
        notifyThemeChangeListeners();
    }

    // Helper to set up the look and feel for LIGHT or DARK
    private static void setupTheme(ThemeMode mode)
    {
        if (mode == ThemeMode.DARK)
        {
            FlatDarkLaf.setup();
        }
        else
        {
            FlatLightLaf.setup();
        }
        refreshAllWindows();
    }

    /**
     * Applies the auto theme, detecting the system's preferred theme.
     * Optionally runs a callback after the theme is applied.
     * @param onThemeChanged Callback to run after theme change (may be null)
     */
    public static void applyAutoTheme(Runnable onThemeChanged)
    {
        applyTheme(ThemeMode.AUTO);
        if (onThemeChanged != null) onThemeChanged.run();
    }
    /**
     * Applies the auto theme without a callback.
     */
    public static void applyAutoTheme()
    {
        applyTheme(ThemeMode.AUTO);
    }

    /**
     * Initializes the theme system and sets the initial theme.
     * Registers listeners for OS theme changes and KDE config changes.
     * @param mode 0 = auto, 1 = light, 2 = dark
     */
    public static void initTheme(int mode)
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        // Listen for Windows and macOS theme changes
        tk.addPropertyChangeListener("win.menu.dark", evt -> applyAutoTheme());
        tk.addPropertyChangeListener("apple.awt.application.appearance", evt -> applyAutoTheme());
        // Listen for GNOME theme changes
        Toolkit.getDefaultToolkit().addPropertyChangeListener("org.gnome.desktop.interface.color-scheme", evt -> applyAutoTheme());
        // Watch KDE config file for theme changes
        watchKdeConfig();

        // Set the initial theme based on the mode parameter
        switch (mode)
        {
            case 1 -> applyTheme(ThemeMode.LIGHT);
            case 2 -> applyTheme(ThemeMode.DARK);
            default -> applyTheme(ThemeMode.AUTO);
        }
    }

    /**
     * Detects if the system's preferred theme is dark.
     * Supports Windows, macOS, GNOME, KDE, and WSL environments.
     * @return true if dark mode is preferred, false otherwise
     */
    private static boolean isSystemDark()
    {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWSL = os.contains("linux") && System.getenv("WSL_DISTRO_NAME") != null;
        // macOS: check system property for dark appearance
        if (os.contains("mac") || os.contains("darwin"))
            return System.getProperty("apple.awt.application.appearance", "").contains("dark");
        // Windows or WSL: query registry for AppsUseLightTheme
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
                        if (line.contains("AppsUseLightTheme")) {
                            String[] parts = line.trim().split("\\s+");
                            int value = Integer.decode(parts[parts.length - 1]);
                            return value == 0; // 0 = dark, 1 = light
                        }
                    }
                }
            }
            catch (Exception ignored)
            {
            }
            // Fallback for IDEs or unknown cases
            return "true".equalsIgnoreCase(System.getProperty("ide.win.menu.dark"));
        }
        // Linux: try multiple mechanisms
        if (os.contains("linux"))
        {
            int theme = readLinuxTheme();
            if (theme == 1) return true;   // dark
            if (theme == 2) return false;  // light
            // fallback: unknown, default to light
            return false;
        }
        return false;
    }

    /**
     * Try to detect Linux theme using multiple mechanisms.
     * Returns: 1 = dark, 2 = light, 0 = unknown
     */
    private static int readLinuxTheme()
    {
        // 1. Try gsettings (GNOME, Cinnamon, etc.)
        try
        {
            Process p = new ProcessBuilder
            (
                "gsettings", "get", "org.gnome.desktop.interface", "color-scheme"
            ).start();
            p.waitFor();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream())))
            {
                String line = r.readLine();
                if (line != null)
                {
                    if (line.contains("prefer-dark")) return 1;
                    if (line.contains("prefer-light")) return 2;
                }
            }
        } catch (Exception ignored) {}

        // 2. Try gdbus (freedesktop portal, used by GNOME, KDE, etc.)
        try
        {
            ProcessBuilder pb = new ProcessBuilder
            (
                "gdbus", "call", "--session", "--timeout=1000",
                "--dest=org.freedesktop.portal.Desktop",
                "--object-path", "/org/freedesktop/portal/desktop",
                "--method", "org.freedesktop.portal.Settings.Read",
                "org.freedesktop.appearance", "color-scheme"
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
            {
                String line = reader.readLine();
                if (line != null)
                {
                    if (line.contains("<<uint32 1>>")) return 1; // dark
                    if (line.contains("<<uint32 2>>")) return 2; // light
                }
            }
            process.waitFor();
        } catch (Exception ignored) {}

        // 3. Try KDE config
        Path cfg = Paths.get(System.getProperty("user.home"), ".config", "kdeglobals");
        if (Files.exists(cfg))
        {
            try (Stream<String> lines = Files.lines(cfg))
            {
                boolean inGeneral = false;
                for (String line : (Iterable<String>) lines::iterator)
                {
                    line = line.trim();
                    if (line.equals("[General]")) inGeneral = true;
                    else if (inGeneral && line.startsWith("ColorScheme="))
                    {
                        return line.substring(line.indexOf('=') + 1).toLowerCase().contains("dark") ? 1 : 2;
                    }
                }
            }
            catch (IOException ignored) {}
        }

        // 4. Try environment variable (some distros set GTK_THEME or similar)
        String gtkTheme = System.getenv("GTK_THEME");
        if (gtkTheme != null && gtkTheme.toLowerCase().contains("dark")) return 1;
        if (gtkTheme != null && gtkTheme.toLowerCase().contains("light")) return 2;

        // 5. Try XDG_CURRENT_DESKTOP or DESKTOP_SESSION for known dark sessions
        String desktop = System.getenv("XDG_CURRENT_DESKTOP");
        if (desktop != null && desktop.toLowerCase().contains("dark")) return 1;

        // Unknown
        return 0;
    }

    /**
     * Returns true if the current theme (manual or auto) is dark.
     * @return true if dark theme is active or preferred
     */
    public static boolean isDarkPreferred()
    {
        if (currentTheme == ThemeMode.DARK) return true;
        if (currentTheme == ThemeMode.LIGHT) return false;
        return isSystemDark();
    }

    /**
     * Refreshes the look and feel of all open windows to match the current theme.
     * This ensures that all UI components update their appearance.
     */
    private static void refreshAllWindows()
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
     * Watches the KDE config directory for changes to kdeglobals and applies the auto theme if it changes.
     * Runs in a background daemon thread.
     */
    private static void watchKdeConfig()
    {
        Path dir = Paths.get(System.getProperty("user.home"), ".config");
        try
        {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            Thread t = new Thread(() ->
            {
                while (true)
                {
                    try
                    {
                        WatchKey key = watcher.take();
                        for (WatchEvent<?> ev : key.pollEvents())
                        {
                            if ("kdeglobals".equals(ev.context().toString()))
                            {
                                applyAutoTheme();
                            }
                        }
                        key.reset();
                    }
                    catch (InterruptedException ignored)
                    {
                        break;
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }
        catch (IOException ignored) {}
    }

    /**
     * Checks if the application is running under Windows Subsystem for Linux (WSL).
     * @return true if running under WSL, false otherwise
     */
    public static boolean isRunningUnderWSL()
    {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("linux") && System.getenv("WSL_DISTRO_NAME") != null;
    }

    /**
     * Starts a timer to poll for theme changes under WSL, since native theme events are not available.
     * Calls the provided callback if the theme changes.
     * @param onThemeChanged callback to run when theme changes (e.g., update window icon)
     */
    public static void startWSLThemePolling(java.util.function.Consumer<Boolean> onThemeChanged)
    {
        if (!isRunningUnderWSL()) return;
        final boolean[] lastDarkMode = { isDarkPreferred() };
        // Timer checks every 2 seconds for theme changes.
        javax.swing.Timer wslThemeTimer = new javax.swing.Timer(2000, null);
        wslThemeTimer.addActionListener(e ->
        {
            boolean dark = isDarkPreferred();
            if (dark != lastDarkMode[0])
            {
                if (onThemeChanged != null) onThemeChanged.accept(dark);
                lastDarkMode[0] = dark;
            }
        });
        wslThemeTimer.setRepeats(true);
        wslThemeTimer.start();
    }
}
