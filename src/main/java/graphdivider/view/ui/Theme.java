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

public final class Theme
{
    private Theme() {}

    // --- Add theme change listeners support ---
    private static final List<Runnable> themeListeners = new CopyOnWriteArrayList<>();

    public static void addThemeChangeListener(Runnable listener) {
        themeListeners.add(listener);
    }

    public static void removeThemeChangeListener(Runnable listener) {
        themeListeners.remove(listener);
    }

    private static void notifyThemeChangeListeners() {
        for (Runnable r : themeListeners) r.run();
        updateAllEdgesColor(); // Notify all Edge components to update color
    }

    // --- New: Notify all Edge components to update their color ---
    public static void updateAllEdgesColor() {
        graphdivider.view.ui.graph.Edge.updateAllEdgesColor();
    }

    public static void applyAutoTheme(Runnable onThemeChanged)
    {
        if (isDarkPreferred()) applyDarkTheme();
        else applyLightTheme();
        if (onThemeChanged != null) onThemeChanged.run();
        notifyThemeChangeListeners(); // Notify listeners and update edges
        refreshAllWindows(); // Ensure all windows/components are updated
    }
    public static void applyAutoTheme()
    {
        applyAutoTheme(null);
    }

    public static void applyLightTheme()
    {
        FlatLightLaf.setup();
        notifyThemeChangeListeners(); // Notify listeners and update edges
        refreshAllWindows();
    }

    public static void applyDarkTheme()
    {
        FlatDarkLaf.setup();
        notifyThemeChangeListeners(); // Notify listeners and update edges
        refreshAllWindows();
    }

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

    public static boolean isDarkPreferred()
    {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWSL = os.contains("linux") && System.getenv("WSL_DISTRO_NAME") != null;
        if (os.contains("mac") || os.contains("darwin"))
            return System.getProperty("apple.awt.application.appearance", "").contains("dark");
        if (os.contains("win") || isWSL)
        {
            try {
                String regExe = isWSL ? "/mnt/c/Windows/System32/reg.exe" : "reg";
                Process process = Runtime.getRuntime().exec(new String[] {
                    regExe, "query",
                    "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v", "AppsUseLightTheme"
                });
                process.waitFor();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
                {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("AppsUseLightTheme")) {
                            String[] parts = line.trim().split("\\s+");
                            int value = Integer.decode(parts[parts.length - 1]);
                            return value == 0;
                        }
                    }
                }
            }
            catch (Exception ignored) {}
            return "true".equalsIgnoreCase(System.getProperty("ide.win.menu.dark"));
        }
        return isGnomeDark() || isKdeDark();
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
                                applyAutoTheme();
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
}
