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

// Utility class for managing application themes (light, dark, auto).
public final class Theme
{
    // Thread-safe list of listeners to notify when the theme changes
    private static final List<Runnable> themeListeners = new CopyOnWriteArrayList<>();
    // Tracks the current theme mode (auto, light, dark)
    private static ThemeMode currentTheme = ThemeMode.AUTO;

    // Private constructor to prevent instantiation of utility class
    private Theme() {}

    // Registers a listener to be notified when the theme changes.
    public static void addThemeChangeListener(Runnable listener)
    {
        themeListeners.add(listener);
    }

    // Removes a previously registered theme change listener.
    public static void removeThemeChangeListener(Runnable listener)
    {
        themeListeners.remove(listener);
    }

    // Notifies all registered listeners that the theme has changed.
    private static void notifyThemeChangeListeners()
    {
        for (Runnable r : themeListeners) r.run();
    }

    // Updates the color of all edges in the graph to match the current theme.
    public static void updateAllEdgesColor() {
        graphdivider.view.ui.graph.Edge.updateAllEdgesColor();
    }

    // Applies the auto theme, detecting the system's preferred theme.
    public static void applyAutoTheme(Runnable onThemeChanged)
    {
        currentTheme = ThemeMode.AUTO;
        if (isSystemDark()) applyDarkTheme();
        else applyLightTheme();
        if (onThemeChanged != null) onThemeChanged.run();
    }

    // Applies the auto theme without a callback.
    public static void applyAutoTheme()
    {
        applyAutoTheme(null);
    }

    // Applies the light theme and notifies listeners.
    public static void applyLightTheme()
    {
        currentTheme = ThemeMode.LIGHT;
        FlatLightLaf.setup();
        refreshAllWindows();
        notifyThemeChangeListeners();
    }

    // Applies the dark theme and notifies listeners.
    public static void applyDarkTheme()
    {
        currentTheme = ThemeMode.DARK;
        FlatDarkLaf.setup();
        refreshAllWindows();
        notifyThemeChangeListeners();
    }

    // Initializes the theme system and sets the initial theme.
    // Registers listeners for OS theme changes and KDE config changes.
    // mode: 0 = auto, 1 = light, 2 = dark
    public static void initTheme(int mode)
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        tk.addPropertyChangeListener("win.menu.dark", evt -> applyAutoTheme());
        tk.addPropertyChangeListener("apple.awt.application.appearance", evt -> applyAutoTheme());
        Toolkit.getDefaultToolkit().addPropertyChangeListener("org.gnome.desktop.interface.color-scheme", evt -> applyAutoTheme());
        watchKdeConfig();

        switch (mode)
        {
            case 1 -> applyLightTheme();
            case 2 -> applyDarkTheme();
            default -> applyAutoTheme();
        }
    }

    // Detects if the system's preferred theme is dark.
    // Supports Windows, macOS, GNOME, KDE, and WSL environments.
    private static boolean isSystemDark()
    {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWSL = os.contains("linux") && System.getenv("WSL_DISTRO_NAME") != null;
        if (os.contains("mac") || os.contains("darwin"))
            return System.getProperty("apple.awt.application.appearance", "").contains("dark");
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

    // Returns true if the current theme (manual or auto) is dark.
    public static boolean isDarkPreferred()
    {
        if (currentTheme == ThemeMode.DARK) return true;
        if (currentTheme == ThemeMode.LIGHT) return false;
        return isSystemDark();
    }

    // Refreshes the look and feel of all open windows to match the current theme.
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

    // Checks if GNOME desktop is using a dark color scheme.
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

    // Checks if KDE desktop is using a dark color scheme by reading the kdeglobals config.
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
                    if (line.equals("[General]")) inGeneral = true;
                    else if (inGeneral && line.startsWith("ColorScheme="))
                        return line.substring(line.indexOf('=')+1).toLowerCase().contains("dark");
                }
            }
            catch (IOException ignored) {}
        }
        return false;
    }

    // Watches the KDE config directory for changes to kdeglobals and applies the auto theme if it changes.
    // Runs in a background daemon thread.
    private static void watchKdeConfig()
    {
        Path dir = Paths.get(System.getProperty("user.home"), ".config");
        try
        {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            Thread t = new Thread(() ->
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
                                applyAutoTheme();
                            }
                        }
                        key.reset();
                    }
                    catch (InterruptedException ignored)
                    {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }
        catch (IOException ignored) {}
    }

    // Loads an icon that adapts to the current theme (light/dark).
    // Appends "_dark" or "_light" to the base icon filename as appropriate.
    public static ImageIcon loadSystemAwareIcon(String basePath)
    {
        String themedPath = basePath.replace(".png", isDarkPreferred() ? "_dark.png" : "_light.png");
        java.net.URL url = Theme.class.getResource(themedPath);
        if (url == null)
        {
            System.err.println("Warning: Icon resource not found: " + themedPath);
            return null;
        }
        return new ImageIcon(url);
    }

    // Enum representing the available theme modes.
    public enum ThemeMode { AUTO, LIGHT, DARK }
}

