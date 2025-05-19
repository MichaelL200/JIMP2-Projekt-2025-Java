package graphdivider.view;

import graphdivider.view.ui.MenuBar;
import graphdivider.view.ui.Theme;
import graphdivider.view.ui.ToolPanel;
import graphdivider.view.ui.Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * Main application window for the Graph Divider tool.
 * Applies Observer and Command patterns for theme and UI logic.
 */
public class Frame extends JFrame implements PropertyChangeListener
{
    private boolean lastDarkMode;
    private Graph graphPanel; // Add reference

    public Frame()
    {
        setTitle("Graph Divider");
        lastDarkMode = Theme.isDarkPreferred();
        updateWindowIcon(lastDarkMode);

        // Register as observer for theme changes
        Toolkit.getDefaultToolkit().addPropertyChangeListener("win.menu.dark", this);

        // WSL theme polling
        boolean isWSL = isRunningUnderWSL();
        if (isWSL)
            startWSLThemePolling();

        setInitialWindowSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        maximizeWindow(isWSL);

        MenuBar menuBar = new MenuBar();
        setJMenuBar(menuBar);

        ToolPanel toolPanel = new ToolPanel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolPanel, BorderLayout.WEST);

        graphPanel = new Graph();
        getContentPane().add(graphPanel, BorderLayout.CENTER);

        // Observer pattern for theme switching
        menuBar.addLightThemeListener(e -> switchTheme(false));
        menuBar.addDarkThemeListener(e -> switchTheme(true));
        menuBar.addAutoThemeListener(e -> {
            Theme.applyAutoTheme(() -> {
                updateWindowIcon(Theme.isDarkPreferred());
                if (graphPanel != null) {
                    SwingUtilities.updateComponentTreeUI(graphPanel);
                    graphPanel.repaint();
                }
            });
        });

        // Observer pattern for tool panel changes
        toolPanel.addChangeListener(e -> {
            int parts = toolPanel.getPartitions();
            int margin = toolPanel.getMargin();
            // TODO: Pass these values to the controller for further processing
            System.out.println("Partitions: " + parts + ", Margin: " + margin + "%");
        });
    }

    private void switchTheme(boolean dark) {
        if (dark) Theme.applyDarkTheme();
        else Theme.applyLightTheme();
        updateWindowIcon(Theme.isDarkPreferred());
        if (graphPanel != null) {
            SwingUtilities.updateComponentTreeUI(graphPanel);
            graphPanel.repaint(); // Ensures edge color updates
        }
    }

    private boolean isRunningUnderWSL() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("linux") && System.getenv("WSL_DISTRO_NAME") != null;
    }

    private void setInitialWindowSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int halfWidth  = screenSize.width / 2;
        int halfHeight = (int) (screenSize.height / 1.5);
        setMinimumSize(new Dimension(halfWidth, halfHeight));
        setSize(halfWidth, halfHeight);
    }

    private void maximizeWindow(boolean isWSL) {
        if (isWSL) {
            SwingUtilities.invokeLater(() -> {
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
                    new javax.swing.Timer(100, e -> {
                        setExtendedState(JFrame.MAXIMIZED_BOTH);
                        revalidate();
                        repaint();
                        ((javax.swing.Timer) e.getSource()).stop();
                    }).start();
                }
            });
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    private void startWSLThemePolling() {
        Timer wslThemeTimer = new Timer(2000, null);
        wslThemeTimer.addActionListener(e -> {
            boolean dark = Theme.isDarkPreferred();
            if (dark != lastDarkMode) {
                updateWindowIcon(dark);
                lastDarkMode = dark;
            }
        });
        wslThemeTimer.setRepeats(true);
        wslThemeTimer.start();
    }

    /**
     * Observer update for theme changes.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean dark = Theme.isDarkPreferred();
        if (dark != lastDarkMode) {
            updateWindowIcon(dark);
            lastDarkMode = dark;
        }
    }

    /**
     * Updates the window icon depending on the theme.
     *
     * @param darkMode true to use icon_dark.png, false to use icon_light.png
     */
    public void updateWindowIcon(boolean darkMode)
    {
        String resource = darkMode ? "/icon_dark.png" : "/icon_light.png";
        try
        {
            Image icon = ImageIO.read(getClass().getResourceAsStream(resource));
            setIconImage(icon);
        }
        catch (IOException | IllegalArgumentException e)
        {
            System.err.println("Warning: Unable to load window icon '" + resource + "': " + e.getMessage());
        }
    }
}

