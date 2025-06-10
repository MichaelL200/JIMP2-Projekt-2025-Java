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

// Theme utility for light/dark/auto modes
public final class Theme
{
    // Listeners for theme changes
    private static final List<Runnable> themeListeners = new CopyOnWriteArrayList<>();
    // Current theme mode
    private static ThemeMode currentTheme = ThemeMode.AUTO;

    // Prevent instantiation
    private Theme() {}

    // Add a theme change listener
    public static void addThemeChangeListener(Runnable listener)
    {
        themeListeners.add(listener);
    }

    // Remove a theme change listener
    public static void removeThemeChangeListener(Runnable listener)
    {
        themeListeners.remove(listener);
    }

    // Notify all listeners
    private static void notifyThemeChangeListeners()
    {
        for (Runnable r : themeListeners) r.run();
    }

    // Set auto theme (detect system)
    public static void applyAutoTheme(Runnable onThemeChanged)
    {
        currentTheme = ThemeMode.AUTO;
        if (isSystemDark()) applyDarkTheme();
        else applyLightTheme();
        if (onThemeChanged != null) onThemeChanged.run();
    }

    // Set auto theme (no callback)
    public static void applyAutoTheme()
    {
        applyAutoTheme(null);
    }

    // Set light theme
    public static void applyLightTheme()
    {
        currentTheme = ThemeMode.LIGHT;
        FlatLightLaf.setup();
        refreshAllWindows();
        notifyThemeChangeListeners();
    }

    // Set dark theme
    public static void applyDarkTheme()
    {
        currentTheme = ThemeMode.DARK;
        FlatDarkLaf.setup();
        refreshAllWindows();
        notifyThemeChangeListeners();
    }

    // Init theme system and listen for OS changes
    // mode: 0=auto, 1=light, 2=dark
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

    // Detect if system prefers dark theme
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

    // True if current theme is dark
    public static boolean isDarkPreferred()
    {
        if (currentTheme == ThemeMode.DARK) return true;
        if (currentTheme == ThemeMode.LIGHT) return false;
        return isSystemDark();
    }

    // Refresh all windows for new theme
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

    // GNOME: check if dark mode is set
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

    // KDE: check if dark mode is set in kdeglobals
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

    // Watch KDE config for changes and auto-update theme
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

    // Load system-aware window icon based on theme
    public static Image loadSystemAwareWindowIcon()
    {
        String resource = isDarkPreferred() ? "/icon/icon_dark.png" : "/icon/icon_light.png";
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

    // Theme mode enum
    public enum ThemeMode { AUTO, LIGHT, DARK }
}