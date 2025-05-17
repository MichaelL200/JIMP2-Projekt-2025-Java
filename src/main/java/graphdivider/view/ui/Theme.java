package graphdivider.view.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * Centralized manager for application themes.
 *
 * Modes:
 *   • Auto — detect OS or DE dark mode where available, else use light
 *   • Light — force FlatLightLaf
 *   • Dark  — force FlatDarkLaf
 *
 * After installing the L&F, all open windows are immediately refreshed.
 */
public final class Theme
{
    private Theme()
    {}

    /**
     * Apply Auto theme. It chooses dark or light based on OS or desktop environment.
     */
    public static void applyAutoTheme(Runnable onThemeChanged)
    {
        boolean dark = isDarkPreferred();
        if (dark)
            applyDarkTheme();
        else
            applyLightTheme();

        if (onThemeChanged != null)
            onThemeChanged.run();
    }
    public static void applyAutoTheme()
    {
        applyAutoTheme(null);
    }

    /**
     * Force the light theme via FlatLightLaf.
     */
    public static void applyLightTheme()
    {
        FlatLightLaf.setup();
        refreshAllWindows();
    }

    /**
     * Force the dark theme via FlatDarkLaf.
     */
    public static void applyDarkTheme()
    {
        FlatDarkLaf.setup();
        refreshAllWindows();
    }

    /**
     * Initialize theme on startup and listen for OS/DE theme changes.
     *
     * @param mode 0=Auto, 1=Light, 2=Dark
     */
    public static void initTheme(int mode)
    {
        // Listen for Windows dark-mode toggle
        Toolkit.getDefaultToolkit().addPropertyChangeListener(
            "win.menu.dark",
            evt -> applyAutoTheme()
        );

        // Listen for macOS appearance change
        Toolkit.getDefaultToolkit().addPropertyChangeListener(
            "apple.awt.application.appearance",
            evt -> applyAutoTheme()
        );

        // Listen for GNOME changes via gsettings
        addDesktopPropertyListener("org.gnome.desktop.interface.color-scheme");

        // Listen for KDE config file changes
        watchKdeConfig();

        // Apply initial mode
        switch (mode)
        {
            case 1 -> applyLightTheme();
            case 2 -> applyDarkTheme();
            default -> applyAutoTheme();
        }
    }

    public static boolean isDarkPreferred()
    {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac") || os.contains("darwin"))
        {
            String appearance = System.getProperty("apple.awt.application.appearance");
            return appearance != null && appearance.contains("dark");
        }
        if (os.contains("win"))
        {
            String theme = System.getProperty("ide.win.menu.dark");
            return "true".equalsIgnoreCase(theme);
        }
        // On Linux, check GNOME and KDE
        if (isGnomeDark() || isKdeDark())
        {
            return true;
        }
        return false;
    }

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

    // GNOME detection via gsettings
    private static boolean isGnomeDark()
    {
        try
        {
            Process p = new ProcessBuilder(
                "gsettings", "get",
                "org.gnome.desktop.interface",
                "color-scheme"
            ).start();
            p.waitFor();
            try (BufferedReader r = new BufferedReader(
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

    // KDE detection via parsing ~/.config/kdeglobals
    private static boolean isKdeDark()
    {
        Path cfg = Paths.get(
            System.getProperty("user.home"),
            ".config",
            "kdeglobals"
        );
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
                        String cs = line.substring(line.indexOf('=')+1).toLowerCase();
                        return cs.contains("dark");
                    }
                }
            }
            catch (IOException e)
            {
                // ignore
            }
        }
        return false;
    }

    // Listen to gsettings changes on GNOME
    private static void addDesktopPropertyListener(String property)
    {
        PropertyChangeListener l = evt -> applyAutoTheme();
        Toolkit.getDefaultToolkit().addPropertyChangeListener(property, l);
    }

    // Watch KDE config file for changes
    private static void watchKdeConfig()
    {
        Path dir = Paths.get(
            System.getProperty("user.home"),
            ".config"
        );
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
        catch (IOException ignored)
        {
            // cannot watch KDE config
        }
    }

    /**
    * Load icon variant based on system dark mode preference.
    *
    * @param basePath path without "_light" or "_dark", e.g., "/icons/icon.png"
    * @return the appropriate ImageIcon for the system theme
    */
    public static ImageIcon loadSystemAwareIcon(String basePath)
    {
        String themedPath = basePath.replace(".png", isDarkPreferred() ? "_dark.png" : "_light.png");
        return new ImageIcon(Theme.class.getResource(themedPath));
    }
}
